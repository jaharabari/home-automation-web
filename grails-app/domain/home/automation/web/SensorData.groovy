package home.automation.web

class SensorData {

    String name
    Double valueOf = 0
    Date dateCreated

    static mapping = {
        sort "dateCreated"
        version false
    }
}
