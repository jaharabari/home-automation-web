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
                initDevelopmentData()
                break;
            case Environment.TEST:
                break
            case Environment.PRODUCTION:
                initData(servletContext)
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
        final def mqttClient = new MqttClient((String) grailsApplication.config.grails.mqtt.brokerUrl,
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

                    /*
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
                            [key: 'recreation', value: payload[11] == 1],
                            [key: 'sockets_bedroom_left', value: payload[12] == 1],
                            [key: 'sockets_bedroom_right', value: payload[13] == 1]
                        ]
                    )
                    */

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
                } else if (s.equals('buttons/'+ Constants.room_porch)) {
                    def value = servletContext.getAttribute(Constants.room_porch) ?: 0
                    connectAndPublish('relays/' + Constants.room_porch +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.room)) {
                    def value = servletContext.getAttribute(Constants.room) ?: 0
                    connectAndPublish('relays/' + Constants.room +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.counter)) {
                    def value = servletContext.getAttribute(Constants.counter) ?: 0
                    connectAndPublish('relays/' + Constants.counter +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.kitchen)) {
                    def value = servletContext.getAttribute(Constants.kitchen) ?: 0
                    connectAndPublish('relays/' + Constants.kitchen +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.bathroom)) {
                    def value = servletContext.getAttribute(Constants.bathroom) ?: 0
                    connectAndPublish('relays/' + Constants.bathroom +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.corridor)) {
                    def value = servletContext.getAttribute(Constants.corridor) ?: 0
                    connectAndPublish('relays/' + Constants.corridor +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.entry)) {
                    def value = servletContext.getAttribute(Constants.entry) ?: 0
                    connectAndPublish('relays/' + Constants.entry +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.bedroom)) {
                    def value = servletContext.getAttribute(Constants.bedroom) ?: 0
                    connectAndPublish('relays/' + Constants.bedroom +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.bedroom_porch)) {
                    def value = servletContext.getAttribute(Constants.bedroom_porch) ?: 0
                    connectAndPublish('relays/' + Constants.bedroom_porch +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.laundry)) {
                    def value = servletContext.getAttribute(Constants.laundry) ?: 0
                    connectAndPublish('relays/' + Constants.laundry +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.upper)) {
                    def value = servletContext.getAttribute(Constants.upper) ?: 0
                    connectAndPublish('relays/' + Constants.upper +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.recreation)) {
                    def value = servletContext.getAttribute(Constants.recreation) ?: 0
                    connectAndPublish('relays/' + Constants.recreation +'/set', value == 1 ? '0' : '1')
                }
            }

            @Override
            void connectionLost(Throwable throwable) {
                println 'connectionLost '

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
                println 'Connected'
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
        println 'Connected'

        mqttSubscribeTopics(mqttClient)
    }

    private static void mqttSubscribeTopics(def mqttClient) {
        mqttClient.subscribe('buttons/' + Constants.room_porch, 0)
        mqttClient.subscribe('buttons/' + Constants.room, 0)
        mqttClient.subscribe('buttons/' + Constants.counter, 0)
        mqttClient.subscribe('buttons/' + Constants.kitchen, 0)
        mqttClient.subscribe('buttons/' + Constants.bathroom, 0)
        mqttClient.subscribe('buttons/' + Constants.corridor, 0)
        mqttClient.subscribe('buttons/' + Constants.entry, 0)
        mqttClient.subscribe('buttons/' + Constants.bedroom, 0)
        mqttClient.subscribe('buttons/' + Constants.bedroom_porch, 0)
        mqttClient.subscribe('buttons/' + Constants.laundry, 0)
        mqttClient.subscribe('buttons/' + Constants.upper, 0)
        mqttClient.subscribe('buttons/' + Constants.recreation, 0)
        mqttClient.subscribe('sensors/temperature', 0)
        mqttClient.subscribe('sensors/humidity', 0)
        mqttClient.subscribe('switches/status', 0)
    }

    void connectAndPublish(String topic, String content) {
        def persistence = new MemoryPersistence()
        def client = new MqttClient((String) grailsApplication.config.grails.mqtt.brokerUrl,
                (String) grailsApplication.config.grails.mqtt.clientId,
                persistence)
        def connOpts = new MqttConnectOptions()
        connOpts.setCleanSession(true)
        client.connect(connOpts)
        def message = new MqttMessage(content.getBytes())
        message.setQos(0)
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

    private static final sendTheThingsIO(def values) {
        def token = 'dFx2IA0cx1EO9WiPNGlEJf_rRrFidecUbP2N_8awRbU'
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
            if (response.statusLine.statusCode == 201) {
                def result = JSON.parse(entity.getContent(), 'UTF-8')
                println result
            }
        }
    }
}
