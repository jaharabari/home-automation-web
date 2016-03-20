import grails.converters.JSON
import grails.util.Environment
import home.automation.web.Constants
import home.automation.web.SensorData
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

    def init = { ServletContext servletContext ->
        switch (Environment.getCurrent()){
            case Environment.DEVELOPMENT:
                initData(servletContext)
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
                if (s.equals('sensors/status')) {
                    def json = JSON.parse(new ByteArrayInputStream(mqttMessage.payload), 'UTF-8')
                    SensorData.withNewSession {
                        def temperature = new BigDecimal(json.temperature)
                        temperature = temperature.setScale(1, BigDecimal.ROUND_HALF_UP)
                        new SensorData(name: 'temperature', valueOf: temperature.doubleValue(), dateHappened: date).save(flush: true)

                        def humidity = new BigDecimal(json.humidity)
                        humidity = humidity.setScale(1, BigDecimal.ROUND_HALF_UP)
                        new SensorData(name: 'humidity', valueOf: humidity.doubleValue(), dateHappened: date).save(flush: true)
                    }

                    brokerMessagingTemplate.convertAndSend '/topic/sensors/status', json
                } else if (s.equals('switches/status')) {
                    def json = JSON.parse(new ByteArrayInputStream(mqttMessage.payload), 'UTF-8')

                    brokerMessagingTemplate.convertAndSend '/topic/switches/status', json

                    servletContext.setAttribute(Constants.lights_room_balcony, json.lights_room_balcony)
                    servletContext.setAttribute(Constants.lights_room, json.lights_room)
                    servletContext.setAttribute(Constants.lights_room_kitchen, json.lights_room_kitchen)
                    servletContext.setAttribute(Constants.lights_kitchen, json.lights_kitchen)
                    servletContext.setAttribute(Constants.lights_bathroom, json.lights_bathroom)
                    servletContext.setAttribute(Constants.lights_bathroom_mirror, json.lights_bathroom_mirror)
                    servletContext.setAttribute(Constants.lights_hall, json.lights_hall)
                    servletContext.setAttribute(Constants.lights_entry_balcony, json.lights_entry_balcony)
                    servletContext.setAttribute(Constants.lights_bedroom, json.lights_bedroom)
                    servletContext.setAttribute(Constants.lights_bedroom_balcony, json.lights_bedroom_balcony)
                    servletContext.setAttribute(Constants.lights_upper_bedroom, json.lights_upper_bedroom)
                    servletContext.setAttribute(Constants.lights_service_area, json.lights_service_area)
                    servletContext.setAttribute(Constants.lights_green_roof, json.lights_green_roof)
                    servletContext.setAttribute(Constants.sockets_bedroom_left, json.sockets_bedroom_left)
                    servletContext.setAttribute(Constants.sockets_bedroom_right, json.sockets_bedroom_right)
                } else if (s.equals('buttons/'+ Constants.lights_room_balcony)) {
                    def value = servletContext.getAttribute(Constants.lights_room_balcony) ?: 0
                    connectAndPublish('relays/' + Constants.lights_room_balcony +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.lights_room)) {
                    def value = servletContext.getAttribute(Constants.lights_room) ?: 0
                    connectAndPublish('relays/' + Constants.lights_room +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.lights_room_kitchen)) {
                    def value = servletContext.getAttribute(Constants.lights_room_kitchen) ?: 0
                    connectAndPublish('relays/' + Constants.lights_room_kitchen +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.lights_kitchen)) {
                    def value = servletContext.getAttribute(Constants.lights_kitchen) ?: 0
                    connectAndPublish('relays/' + Constants.lights_kitchen +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.lights_bathroom)) {
                    def value = servletContext.getAttribute(Constants.lights_bathroom) ?: 0
                    connectAndPublish('relays/' + Constants.lights_bathroom +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.lights_bathroom_mirror)) {
                    def value = servletContext.getAttribute(Constants.lights_bathroom_mirror) ?: 0
                    connectAndPublish('relays/' + Constants.lights_bathroom_mirror +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.lights_hall)) {
                    def value = servletContext.getAttribute(Constants.lights_hall) ?: 0
                    connectAndPublish('relays/' + Constants.lights_hall +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.lights_entry_balcony)) {
                    def value = servletContext.getAttribute(Constants.lights_entry_balcony) ?: 0
                    connectAndPublish('relays/' + Constants.lights_entry_balcony +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.lights_bedroom)) {
                    def value = servletContext.getAttribute(Constants.lights_bedroom) ?: 0
                    connectAndPublish('relays/' + Constants.lights_bedroom +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.lights_bedroom_balcony)) {
                    def value = servletContext.getAttribute(Constants.lights_bedroom_balcony) ?: 0
                    connectAndPublish('relays/' + Constants.lights_bedroom_balcony +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.lights_upper_bedroom)) {
                    def value = servletContext.getAttribute(Constants.lights_upper_bedroom) ?: 0
                    connectAndPublish('relays/' + Constants.lights_upper_bedroom +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.lights_service_area)) {
                    def value = servletContext.getAttribute(Constants.lights_service_area) ?: 0
                    connectAndPublish('relays/' + Constants.lights_service_area +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.lights_green_roof)) {
                    def value = servletContext.getAttribute(Constants.lights_green_roof) ?: 0
                    connectAndPublish('relays/' + Constants.lights_green_roof +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.sockets_bedroom_left)) {
                    def value = servletContext.getAttribute(Constants.sockets_bedroom_left) ?: 0
                    connectAndPublish('relays/' + Constants.sockets_bedroom_left +'/set', value == 1 ? '0' : '1')
                } else if (s.equals('buttons/'+ Constants.sockets_bedroom_right)) {
                    def value = servletContext.getAttribute(Constants.sockets_bedroom_right) ?: 0
                    connectAndPublish('relays/' + Constants.sockets_bedroom_right +'/set', value == 1 ? '0' : '1')
                }
            }

            @Override
            void connectionLost(Throwable throwable) {
                println 'connectionLost'
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

    private void mqttSubscribeTopics(def mqttClient) {
        mqttClient.subscribe('buttons/' + Constants.lights_room_balcony, 0)
        mqttClient.subscribe('buttons/' + Constants.lights_room, 0)
        mqttClient.subscribe('buttons/' + Constants.lights_room_kitchen, 0)
        mqttClient.subscribe('buttons/' + Constants.lights_kitchen, 0)
        mqttClient.subscribe('buttons/' + Constants.lights_bathroom, 0)
        mqttClient.subscribe('buttons/' + Constants.lights_bathroom_mirror, 0)
        mqttClient.subscribe('buttons/' + Constants.lights_hall, 0)
        mqttClient.subscribe('buttons/' + Constants.lights_entry_balcony, 0)
        mqttClient.subscribe('buttons/' + Constants.lights_bedroom, 0)
        mqttClient.subscribe('buttons/' + Constants.lights_bedroom_balcony, 0)
        mqttClient.subscribe('buttons/' + Constants.lights_upper_bedroom, 0)
        mqttClient.subscribe('buttons/' + Constants.lights_service_area, 0)
        mqttClient.subscribe('buttons/' + Constants.lights_green_roof, 0)
        mqttClient.subscribe('buttons/' + Constants.sockets_bedroom_left, 0)
        mqttClient.subscribe('buttons/' + Constants.sockets_bedroom_right, 0)
        mqttClient.subscribe('sensors/status', 0)
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

    public static double randomInRange(Random random, double min, double max) {
        double range = max - min
        double scaled = random.nextDouble() * range
        double shifted = scaled + min
        return shifted // == (rand.nextDouble() * (max-min)) + min
    }
}
