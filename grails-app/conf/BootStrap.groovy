import grails.converters.JSON
import grails.util.Environment
import home.automation.web.Constants
import home.automation.web.SensorData
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.joda.time.DateTime
import org.springframework.messaging.simp.SimpMessagingTemplate

import javax.servlet.ServletContext

class BootStrap {

    def grailsApplication
    SimpMessagingTemplate brokerMessagingTemplate
    def isFirstRun = true

    def init = { ServletContext servletContext ->
        switch (Environment.getCurrent()){
            case Environment.DEVELOPMENT:
                initData(servletContext)
                initDataCloud(servletContext)
                initDevelopmentData()
                break;
            case Environment.TEST:
                break
            case Environment.PRODUCTION:
                initData(servletContext)
                initDataCloud(servletContext)
                break
        }
    }

    def destroy = {
    }

    void initData(ServletContext servletContext) {
        isFirstRun = SensorData.count == 0

        def dataStore = new MemoryPersistence()
        def conOpt = new MqttConnectOptions()
        conOpt.setCleanSession(true)
        final def mqttClient = new MqttClient((String) grailsApplication.config.localmqtt.host + ":" +
                (String) grailsApplication.config.localmqtt.port,
                (String) grailsApplication.config.grails.mqtt.clientId + '-sub',
                dataStore)
        mqttClient.setCallback(new MqttCallback(){
            @Override
            void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                def date = new Date()
                println date.format('yyyy-MM-dd-HH-mm-ss') + ' mqtt_messageArrived:' + s
                if (s.equals('sensors/temperature')) {
                    def str = new String(mqttMessage.payload)
                    def value = new BigDecimal(Double.parseDouble(str.trim()))

                    SensorData.withNewSession {
                        value = value.setScale(1, BigDecimal.ROUND_HALF_UP)
                        new SensorData(name: 'temperature', valueOf: value.doubleValue(), dateHappened: date).save(flush: true)
                    }

                    brokerMessagingTemplate.convertAndSend '/topic/sensors/temperature', str.trim()

                    sendTheThingsIO([[key: 'temperature', value: value.doubleValue()]])
                } else if (s.equals('sensors/humidity')) {
                    def str = new String(mqttMessage.payload)
                    def value = new BigDecimal(Double.parseDouble(str.trim()))

                    SensorData.withNewSession {
                        value = value.setScale(1, BigDecimal.ROUND_HALF_UP)
                        new SensorData(name: 'humidity', valueOf: value.doubleValue(), dateHappened: date).save(flush: true)
                    }

                    brokerMessagingTemplate.convertAndSend '/topic/sensors/humidity', str.trim()

                    sendTheThingsIO([[key: 'humidity', value: value.doubleValue()]])
                } else if (s.equals('switches/status')) {
                    def payload = mqttMessage.payload

                    def json = [
                        room_porch: payload[0],
                        room: payload[1],
                        counter: payload[2],
                        kitchen: payload[3],
                        bathroom: payload[4],
                        corridor: payload[5],
                        entry: payload[6],
                        bedroom: payload[7],
                        bedroom_porch: payload[8],
                        laundry: payload[9],
                        upper: payload[10],
                        recreation: payload[11]
                    ]

                    brokerMessagingTemplate.convertAndSend '/topic/switches/status', json

                    servletContext.setAttribute(Constants.room_porch, json.room_porch)
                    servletContext.setAttribute(Constants.room, json.room)
                    servletContext.setAttribute(Constants.counter, json.counter)
                    servletContext.setAttribute(Constants.kitchen, json.kitchen)
                    servletContext.setAttribute(Constants.bathroom, json.bathroom)
                    servletContext.setAttribute(Constants.corridor, json.corridor)
                    servletContext.setAttribute(Constants.entry, json.entry)
                    servletContext.setAttribute(Constants.bedroom, json.bedroom)
                    servletContext.setAttribute(Constants.bedroom_porch, json.bedroom_balcony)
                    servletContext.setAttribute(Constants.laundry, json.laundry)
                    servletContext.setAttribute(Constants.upper, json.upper)
                    servletContext.setAttribute(Constants.recreation, json.recreation)

                    sendTheThingsIO(
                            [
                                    [key: 'room_porch', value: payload[0] == 1],
                                    [key: 'room', value: payload[1] == 1],
                                    [key: 'counter', value: payload[2] == 1],
                                    [key: 'kitchen', value: payload[3] == 1],
                                    [key: 'bathroom', value: payload[4] == 1],
                                    [key: 'corridor', value: payload[5] == 1],
                                    [key: 'entry', value: payload[6] == 1],
                                    [key: 'bedroom', value: payload[7] == 1],
                                    [key: 'bedroom_porch', value: payload[8] == 1],
                                    [key: 'upper', value: payload[9] == 1],
                                    [key: 'laundry', value: payload[10] == 1],
                                    [key: 'recreation', value: payload[11] == 1]
                            ]
                    )

                    def jsonToCloud = [
                            room_porch: payload[0] == 1 ? 1 : 0,
                            room: payload[1] == 1 ? 1 : 0,
                            counter: payload[2] == 1 ? 1 : 0,
                            kitchen: payload[3] == 1 ? 1 : 0,
                            bathroom: payload[4] == 1 ? 1 : 0,
                            corridor: payload[5] == 1 ? 1 : 0,
                            entry: payload[6] == 1 ? 1 : 0,
                            bedroom: payload[7] == 1 ? 1 : 0,
                            bedroom_porch: payload[8] == 1 ? 1 : 0,
                            laundry: payload[9] == 1 ? 1 : 0,
                            upper: payload[10] == 1 ? 1 : 0,
                            recreation: payload[11] == 1 ? 1 : 0
                    ]
                    // Base64.encode([1,1,1,1,1,1,1,1,1,1,1,1]).toString() == AQEBAQEBAQEBAQEB
                    // Base64.encode([0,0,0,0,0,0,0,0,0,0,0,0]).toString() == AAAAAAAAAAAAAAAA
                    connectAndPublishToCloud('switches/status', (jsonToCloud as JSON).toString(false))
                } else if (s.equals('buttons/room_out_1')) {
                    def value = servletContext.getAttribute(Constants.room_porch) ?: 0
                    connectAndPublish('relays/' + Constants.room_porch +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/room_out_2')) {
                    def value = servletContext.getAttribute(Constants.room) ?: 0
                    connectAndPublish('relays/' + Constants.room +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/room_1')) {
                    def value = servletContext.getAttribute(Constants.counter) ?: 0
                    connectAndPublish('relays/' + Constants.kitchen +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/room_2')) {
                    def value = servletContext.getAttribute(Constants.kitchen) ?: 0
                    connectAndPublish('relays/' + Constants.counter +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/corridor_1')) {
                    def value = servletContext.getAttribute(Constants.bathroom) ?: 0
                    connectAndPublish('relays/' + Constants.entry +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/corridor_2')) {
                    def value = servletContext.getAttribute(Constants.corridor) ?: 0
                    connectAndPublish('relays/' + Constants.room +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/bathroom')) {
                    def value = servletContext.getAttribute(Constants.entry) ?: 0
                    connectAndPublish('relays/' + Constants.bathroom +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/entry')) {
                    def value = servletContext.getAttribute(Constants.bedroom) ?: 0
                    connectAndPublish('relays/' + Constants.entry +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/bedroom')) {
                    def value = servletContext.getAttribute(Constants.bedroom) ?: 0
                    connectAndPublish('relays/' + Constants.bedroom_porch +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/bedroom_out_1')) {
                    def value = servletContext.getAttribute(Constants.laundry) ?: 0
                    connectAndPublish('relays/' + Constants.bedroom_porch +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/bedroom_out_2')) {
                    def value = servletContext.getAttribute(Constants.upper) ?: 0
                    connectAndPublish('relays/' + Constants.bedroom +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/bed_left')) {
                    def value = servletContext.getAttribute(Constants.recreation) ?: 0
                    connectAndPublish('relays/' + Constants.bedroom +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/bed_right')) {
                    def value = servletContext.getAttribute(Constants.recreation) ?: 0
                    connectAndPublish('relays/' + Constants.bedroom +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/laundry')) {
                    def value = servletContext.getAttribute(Constants.recreation) ?: 0
                    connectAndPublish('relays/' + Constants.laundry +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/upper_1')) {
                    def value = servletContext.getAttribute(Constants.recreation) ?: 0
                    connectAndPublish('relays/' + Constants.recreation +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/upper_2')) {
                    def value = servletContext.getAttribute(Constants.recreation) ?: 0
                    connectAndPublish('relays/' + Constants.upper +'/set', value == 1 ? '0' : '1')
                }
            }

            @Override
            void connectionLost(Throwable throwable) {
                println 'mosquitto connectionLost '

                if (throwable)
                    throwable.printStackTrace()

                sleep(1000)

                try {
                    mqttClient.connect(conOpt)
                } catch (e) {
                    println e.getMessage()
                }

                while (!mqttClient.isConnected()) {
                    println 'Trying to connect to mosquitto server...'
                    sleep(1000)
                    try {
                        mqttClient.connect(conOpt)
                    } catch (e) {
                        println e.getMessage()
                    }
                }

                mqttSubscribeTopics(mqttClient)
                println 'Connected mosquitto'
            }

            @Override
            void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            }
        })

        try {
            mqttClient.connect(conOpt)
        } catch (e) {
            println e.getMessage()
        }

        while (!mqttClient.isConnected()) {
            println 'Trying to connect to mosquitto server...'
            sleep(1000)
            try {
                mqttClient.connect(conOpt)
            } catch (e) {
                println e.getMessage()
            }
        }
        println 'Connected mosquitto'

        mqttSubscribeTopics(mqttClient)
    }

    void initDataCloud(ServletContext servletContext) {
        def dataStore = new MemoryPersistence()
        def conOpt = new MqttConnectOptions()
        conOpt.setCleanSession(true)
        conOpt.setUserName(grailsApplication.config.cloudmqtt.user)
        conOpt.setPassword(((String)grailsApplication.config.cloudmqtt.password).chars)
        final def mqttClient = new MqttClient((String) grailsApplication.config.cloudmqtt.host + ":" +
                (String) grailsApplication.config.cloudmqtt.port,
                (String) grailsApplication.config.grails.mqtt.clientId + '-sub',
                dataStore)
        mqttClient.setCallback(new MqttCallback(){
            @Override
            void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                if (s.equals('relays/' + Constants.room_porch + '/set')) {
                    def value = new String(mqttMessage.payload)
                    connectAndPublish(s, value.equals('0') ? '0' : '1')
                } else if (s.equals('relays/' + Constants.room + '/set')) {
                    def value = new String(mqttMessage.payload)
                    connectAndPublish(s, value.equals('0') ? '0' : '1')
                } else if (s.equals('relays/' + Constants.entry + '/set')) {
                    def value = new String(mqttMessage.payload)
                    connectAndPublish(s, value.equals('0') ? '0' : '1')
                } else if (s.equals('relays/' + Constants.counter + '/set')) {
                    def value = new String(mqttMessage.payload)
                    connectAndPublish(s, value.equals('0') ? '0' : '1')
                } else if (s.equals('relays/' + Constants.corridor + '/set')) {
                    def value = new String(mqttMessage.payload)
                    connectAndPublish(s, value.equals('0') ? '0' : '1')
                } else if (s.equals('relays/' + Constants.bathroom + '/set')) {
                    def value = new String(mqttMessage.payload)
                    connectAndPublish(s, value.equals('0') ? '0' : '1')
                } else if (s.equals('relays/' + Constants.bedroom + '/set')) {
                    def value = new String(mqttMessage.payload)
                    connectAndPublish(s, value.equals('0') ? '0' : '1')
                } else if (s.equals('relays/' + Constants.bedroom_porch + '/set')) {
                    def value = new String(mqttMessage.payload)
                    connectAndPublish(s, value.equals('0') ? '0' : '1')
                } else if (s.equals('relays/' + Constants.laundry + '/set')) {
                    def value = new String(mqttMessage.payload)
                    connectAndPublish(s, value.equals('0') ? '0' : '1')
                } else if (s.equals('relays/' + Constants.upper + '/set')) {
                    def value = new String(mqttMessage.payload)
                    connectAndPublish(s, value.equals('0') ? '0' : '1')
                } else if (s.equals('relays/' + Constants.recreation + '/set')) {
                    def value = new String(mqttMessage.payload)
                    connectAndPublish(s, value.equals('0') ? '0' : '1')
                } else if (s.equals('relays/' + Constants.kitchen + '/set')) {
                    def value = new String(mqttMessage.payload)
                    connectAndPublish(s, value.equals('0') ? '0' : '1')
                }
            }

            @Override
            void connectionLost(Throwable throwable) {
                println 'mqttcloud connectionLost '

                if (throwable)
                    throwable.printStackTrace()

                sleep(1000)

                try {
                    mqttClient.connect(conOpt)
                } catch (e) {
                    println e.getMessage()
                }

                while (!mqttClient.isConnected()) {
                    println 'Trying to connect to mqttcloud server...'
                    sleep(1000)
                    try {
                        mqttClient.connect(conOpt)
                    } catch (e) {
                        println e.getMessage()
                    }
                }

                println 'Connected mqttcloud'

                mqttSubscribeRelaysTopics(mqttClient)
            }

            @Override
            void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            }
        })

        try {
            mqttClient.connect(conOpt)
        } catch (e) {
            println e.getMessage()
        }

        while (!mqttClient.isConnected()) {
            println 'Trying to connect to mqttcloud server...'
            sleep(1000)
            try {
                mqttClient.connect(conOpt)
            } catch (e) {
                println e.getMessage()
            }
        }
        println 'Connected mqttcloud'

        mqttSubscribeRelaysTopics(mqttClient)
    }

    private static void mqttSubscribeTopics(def mqttClient) {
        mqttClient.subscribe('buttons/room_out_1', 0)
        mqttClient.subscribe('buttons/room_out_2', 0)
        mqttClient.subscribe('buttons/room_1', 0)
        mqttClient.subscribe('buttons/room_2', 0)
        mqttClient.subscribe('buttons/corridor_1', 0)
        mqttClient.subscribe('buttons/corridor_2', 0)
        mqttClient.subscribe('buttons/bathroom', 0)
        mqttClient.subscribe('buttons/bedroom', 0)
        mqttClient.subscribe('buttons/bedroom_out_1', 0)
        mqttClient.subscribe('buttons/bedroom_out_2', 0)
        mqttClient.subscribe('buttons/bedroom_porch', 0)
        mqttClient.subscribe('buttons/bed_left', 0)
        mqttClient.subscribe('buttons/bed_right', 0)
        mqttClient.subscribe('buttons/upper_1', 0)
        mqttClient.subscribe('buttons/upper_2', 0)
        mqttClient.subscribe('buttons/entry', 0)
        mqttClient.subscribe('buttons/laundry', 0)
        mqttClient.subscribe('sensors/temperature', 0)
        mqttClient.subscribe('sensors/humidity', 0)
        mqttClient.subscribe('switches/status', 0)
    }

    private static void mqttSubscribeRelaysTopics(def mqttClient) {
        mqttClient.subscribe('relays/' + Constants.room_porch + '/set', 0)
        mqttClient.subscribe('relays/' + Constants.room + '/set', 0)
        mqttClient.subscribe('relays/' + Constants.bathroom + '/set', 0)
        mqttClient.subscribe('relays/' + Constants.bedroom_porch + '/set', 0)
        mqttClient.subscribe('relays/' + Constants.bedroom + '/set', 0)
        mqttClient.subscribe('relays/' + Constants.laundry + '/set', 0)
        mqttClient.subscribe('relays/' + Constants.recreation + '/set', 0)
        mqttClient.subscribe('relays/' + Constants.kitchen + '/set', 0)
        mqttClient.subscribe('relays/' + Constants.counter + '/set', 0)
        mqttClient.subscribe('relays/' + Constants.upper + '/set', 0)
        mqttClient.subscribe('relays/' + Constants.entry + '/set', 0)
        mqttClient.subscribe('relays/' + Constants.corridor + '/set', 0)
    }

    void connectAndPublish(String topic, String content) {
        def persistence = new MemoryPersistence()
        def client = new MqttClient((String) grailsApplication.config.localmqtt.host + ":" +
                (String) grailsApplication.config.localmqtt.port,
                (String) grailsApplication.config.grails.mqtt.clientId, persistence)
        def connOpts = new MqttConnectOptions()
        connOpts.setCleanSession(true)
        client.connect(connOpts)
        def message = new MqttMessage(content.getBytes())
        message.setQos(0)
        client.publish(topic, message)
        client.disconnect()
    }

    void connectAndPublishToCloud(String topic, String content) {
        def persistence = new MemoryPersistence()
        def client = new MqttClient((String) grailsApplication.config.cloudmqtt.host + ":" +
                (String) grailsApplication.config.cloudmqtt.port,
                (String) grailsApplication.config.grails.mqtt.clientId, persistence)
        def connOpts = new MqttConnectOptions()
        connOpts.setCleanSession(true)
        connOpts.setUserName(grailsApplication.config.cloudmqtt.user)
        connOpts.setPassword(((String)grailsApplication.config.cloudmqtt.password).chars)
        client.connect(connOpts)
        def message = new MqttMessage(content.getBytes())
        message.setQos(0)
        message.setRetained(true)
        client.publish(topic, message)
        client.disconnect()
    }

    void initDevelopmentData() {
        if (isFirstRun) {
            def random = new Random()
            def minTemp = 20d
            def maxTemp = 31d
            def minHumid = 50d
            def maxHumid = 90d

            (0..336).each {
                def date = new DateTime().plusMinutes((it - 336) * 30)
                def temperature = new BigDecimal(randomInRange(random, minTemp, maxTemp))
                temperature = temperature.setScale(1, BigDecimal.ROUND_HALF_UP)
                new SensorData(name: 'temperature', valueOf: temperature.doubleValue(), dateHappened: date.toDate()).save(flush: true)

                def humidity = new BigDecimal(randomInRange(random, minHumid, maxHumid))
                humidity = humidity.setScale(1, BigDecimal.ROUND_HALF_UP)
                new SensorData(name: 'humidity', valueOf: humidity.doubleValue(), dateHappened: date.toDate()).save(flush: true)
            }
        }
    }

    public static double randomInRange(Random random, double min, double max) {
        double range = max - min
        double scaled = random.nextDouble() * range
        double shifted = scaled + min
        return shifted // == (rand.nextDouble() * (max-min)) + min
    }

    private final sendTheThingsIO(def values) {
        def token = (String) grailsApplication.config.thethingsio.token
        Thread.start {
            def map = [values: values]
            def json = map as JSON
            StringEntity entityPost = new StringEntity(json.toString())
            HttpPost httppost = new HttpPost('https://api.thethings.io/v2/things/' + token)
            httppost.setEntity(entityPost)
            httppost.setHeader("Content-type", "application/json")
            HttpClient httpclient = new DefaultHttpClient()
            HttpResponse response = httpclient.execute(httppost)
            HttpEntity entity = response.getEntity()
            if (response.statusLine.statusCode != 201) {
                println "api.thethings.io erro http ${response.statusCode}"
            }
//            def result = JSON.parse(entity.getContent(), 'UTF-8')
//            println "api.thethings.io: ${result}"
        }
    }
}
