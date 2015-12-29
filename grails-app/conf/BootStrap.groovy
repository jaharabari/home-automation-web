import grails.converters.JSON
import grails.util.Environment
import home.automation.web.SensorData
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class BootStrap {

    def grailsApplication

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
        def client = new MqttClient((String) grailsApplication.config.grails.mqtt.brokerUrl,
                (String) grailsApplication.config.grails.mqtt.clientId + '-sub',
                dataStore)
        client.setCallback(new CustomMqttCallback())
        client.connect(conOpt)
        client.subscribe('lights/room1', 0)
        client.subscribe('status/fmt/json', 0)
    }

    private static class CustomMqttCallback implements MqttCallback {

        @Override
        void connectionLost(Throwable throwable) {
            println 'connectionLost ' + throwable.printStackTrace()
        }

        @Override
        void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
            println 'mqtt:' + s
            if (s.equals('status/fmt/json')) {
                def json = JSON.parse(new ByteArrayInputStream(mqttMessage.payload), 'UTF-8')
                SensorData.withNewSession {
                    def temperature = new BigDecimal(json.temperature)
                    temperature = temperature.setScale(1, BigDecimal.ROUND_HALF_UP)
                    new SensorData(name: 'temperature', valueOf: temperature.doubleValue()).save(flush: true)

                    def humidity = new BigDecimal(json.humidity)
                    humidity = humidity.setScale(1, BigDecimal.ROUND_HALF_UP)
                    new SensorData(name: 'humidity', valueOf: humidity.doubleValue()).save(flush: true)
                }
            }
        }

        @Override
        void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            println 'deliveryComplete ' + iMqttDeliveryToken
        }
    }
}
