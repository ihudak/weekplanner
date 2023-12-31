package eu.dec21.wp.model;

public class Version implements Model {
    private String serviceId;
    private String ver;
    private String date;
    private String status;
    private String message;

    public Version() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Version(String serviceId, String ver, String date, String status, String message) {
        this.serviceId = serviceId;
        this.ver = ver;
        this.date = date;
        this.status = status;
        this.message = message;
    }

    @Override
    public long getId() {
        return 0;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
