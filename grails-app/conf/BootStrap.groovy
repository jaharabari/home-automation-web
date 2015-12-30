import grails.converters.JSON
import grails.util.Environment
import home.automation.web.SensorData
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class BootStrap {

    def grailsApplication
    static int lightsRoom = 0
    static int lightsBedroom = 0
    static int lightsKitchen = 0

    def init = { servletContext ->
        switch (Environment.getCurrent()){
            case Environment.DEVELOPMENT:
                initData()
                break;
            case Environment.TEST:
                break
            case Environment.PRODUCTION:
                initData()
                break
        }
    }

    def destroy = {
    }

    void initData() {
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
                } else if (s.equals('switches/status')) {
                    def json = JSON.parse(new ByteArrayInputStream(mqttMessage.payload), 'UTF-8')

                    println json

                    lightsRoom = json.room
                    lightsBedroom = json.bedroom
                    lightsKitchen = json.kitchen
                } else if (s.equals('buttons/room')) {
                    connectAndPublish('lights/room/set', lightsRoom == 1 ? '0' : '1')
                } else if (s.equals('buttons/bedroom')) {
                    connectAndPublish('lights/bedroom/set', lightsBedroom == 1 ? '0' : '1')
                } else if (s.equals('buttons/kitchen')) {
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
