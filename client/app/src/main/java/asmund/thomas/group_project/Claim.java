package asmund.thomas.group_project;

public class Claim {
    String id;
    String des;
    String photo;
    String location;

    public Claim(String id, String des, String photo, String location) {
        this.id = id;
        this.des = des;
        this.photo = photo;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getDes() {
        return des;
    }

    public String getPhoto() {
        return photo;
    }

    public String getLocation() {
        return location;
    }
}
