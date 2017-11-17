package no.westerdals;

public class Subject {
    private String name;
    private String subjectid;
    private String lecturer;
    private String starttime;
    private String endttime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubjectid() {
        return subjectid;
    }

    public void setSubjectid(String subjectid) {
        this.subjectid = subjectid;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndttime() {
        return endttime;
    }

    public void setEndttime(String endttime) {
        this.endttime = endttime;
    }
}
