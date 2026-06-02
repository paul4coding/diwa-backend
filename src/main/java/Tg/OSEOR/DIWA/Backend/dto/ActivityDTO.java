package Tg.OSEOR.DIWA.Backend.dto;

public class ActivityDTO {
    private String type;
    private String text;
    private String time;
    private String color;

    public ActivityDTO() {}
    
    public ActivityDTO(String type, String text, String time, String color) {
        this.type = type;
        this.text = text;
        this.time = time;
        this.color = color;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
