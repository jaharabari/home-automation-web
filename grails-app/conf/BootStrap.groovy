import grails.converters.JSON
import grails.util.Environment
import home.automation.web.SensorData
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
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
        def dataStore = new MemoryPersistence()
        def conOpt = new MqttConnectOptions()
        conOpt.setCleanSession(true)
        final def mqttClient = new MqttClient((String) grailsApplication.config.grails.mqtt.brokerUrl,
                (String) grailsApplication.config.grails.mqtt.clientId + '-sub',
                dataStore)
        mqttClient.setCallback(new MqttCallback(){

            @Override
            void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                println 'mqtt:' + s
                if (s.equals('sensors/status')) {
                    def json = JSON.parse(new ByteArrayInputStream(mqttMessage.payload), 'UTF-8')
                    SensorData.withNewSession {
                        def temperature = new BigDecimal(json.temperature)
                        temperature = temperature.setScale(1, BigDecimal.ROUND_HALF_UP)
                        new SensorData(name: 'temperature', valueOf: temperature.doubleValue()).save(flush: true)

                        def humidity = new BigDecimal(json.humidity)
                        humidity = humidity.setScale(1, BigDecimal.ROUND_HALF_UP)
                        new SensorData(name: 'humidity', valueOf: humidity.doubleValue()).save(flush: true)
                    }

                    brokerMessagingTemplate.convertAndSend '/topic/sensors/status', json
                } else if (s.equals('switches/status')) {
                    def json = JSON.parse(new ByteArrayInputStream(mqttMessage.payload), 'UTF-8')

                    brokerMessagingTemplate.convertAndSend '/topic/switches/status', json

                    servletContext.setAttribute("lightsRoom", json.room)
                    servletContext.setAttribute("lightsBedroom", json.bedroom)
                    servletContext.setAttribute("lightsKitchen", json.kitchen)
                } else if (s.equals('buttons/room')) {
                    def lightsRoom = servletContext.getAttribute('lightsRoom') ?: 0
                    connectAndPublish('lights/room/set', lightsRoom == 1 ? '0' : '1')
                } else if (s.equals('buttons/bedroom')) {
                    def lightsBedroom = servletContext.getAttribute('lightsBedroom') ?: 0
                    connectAndPublish('lights/bedroom/set', lightsBedroom == 1 ? '0' : '1')
                } else if (s.equals('buttons/kitchen')) {
                    def lightsKitchen = servletContext.getAttribute('lightsKitchen') ?: 0
                    connectAndPublish('lights/kitchen/set', lightsKitchen == 1 ? '0' : '1')
                }
            }

            @Override
            void connectionLost(Throwable throwable) {
                println 'connectionLost ' + throwable.printStackTrace()
            }

            @Override
            void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            }
        })
        mqttClient.connect(conOpt)

        mqttClient.subscribe('buttons/room', 0)
        mqttClient.subscribe('buttons/bedroom', 0)
        mqttClient.subscribe('buttons/kitchen', 0)

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
}
