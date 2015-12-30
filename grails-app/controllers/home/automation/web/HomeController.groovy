package home.automation.web

import grails.converters.JSON
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class HomeController {

    def grailsApplication

    def index() {
        def temperature = null
        def humidity = null

        def temperatures = SensorData.executeQuery('select sd from SensorData as sd where sd.name = :name order by sd.dateCreated desc', [name: 'temperature'], [max: 1])
        if (!temperatures.isEmpty())
            temperature = temperatures.get(0).valueOf

        def humidities = SensorData.executeQuery('select sd from SensorData as sd where sd.name = :name order by sd.dateCreated desc', [name: 'humidity'], [max: 1])
        if (!humidities.isEmpty())
            humidity = humidities.get(0).valueOf

        [temperature: temperature, humidity: humidity]
    }

    def charts() {
    }

    def chartData() {
        def name = params.switchValue.equals('Umidade') ? 'humidity' : 'temperature'
        def list = SensorData.executeQuery('select sd from SensorData as sd where sd.name = :name order by sd.dateCreated asc', [name: name], [max: 25]).collect{
            [value: it.valueOf, time: it.dateCreated.time]
        }
        def result = [error: 0, payload: list]
        render result as JSON
    }

    def publish() {
        def topic = params.topic
        def content = params.message
        def qos = 0
        def persistence = new MemoryPersistence()

        try {
            def client = new MqttClient((String) grailsApplication.config.grails.mqtt.brokerUrl,
                    (String) grailsApplication.config.grails.mqtt.clientId,
                    persistence)
            def connOpts = new MqttConnectOptions()
            connOpts.setCleanSession(true)
            client.connect(connOpts)
            def message = new MqttMessage(content.getBytes())
            message.setQos(qos)
            client.publish(topic, message)
            client.disconnect()

            def result = [error: 0, payload: '']
            render result as JSON
        } catch(MqttException me) {
            me.printStackTrace()

            def result = [error: 1, payload: me.message]
            render result as JSON
        }
    }
}
