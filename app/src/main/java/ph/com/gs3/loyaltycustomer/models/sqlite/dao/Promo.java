package ph.com.gs3.loyaltycustomer.models.sqlite.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "PROMO".
 */
public class Promo {

    private Long id;
    private String name;
    private Integer required_points;
    private Integer required_visit_count;
    private java.util.Date created_at;
    private java.util.Date update_at;
    private Integer is_active;

    public Promo() {
    }

    public Promo(Long id) {
        this.id = id;
    }

    public Promo(Long id, String name, Integer required_points, Integer required_visit_count, java.util.Date created_at, java.util.Date update_at, Integer is_active) {
        this.id = id;
        this.name = name;
        this.required_points = required_points;
        this.required_visit_count = required_visit_count;
        this.created_at = created_at;
        this.update_at = update_at;
        this.is_active = is_active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRequired_points() {
        return required_points;
    }

    public void setRequired_points(Integer required_points) {
        this.required_points = required_points;
    }

    public Integer getRequired_visit_count() {
        return required_visit_count;
    }

    public void setRequired_visit_count(Integer required_visit_count) {
        this.required_visit_count = required_visit_count;
    }

    public java.util.Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(java.util.Date created_at) {
        this.created_at = created_at;
    }

    public java.util.Date getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(java.util.Date update_at) {
        this.update_at = update_at;
    }

    public Integer getIs_active() {
        return is_active;
    }

    public void setIs_active(Integer is_active) {
        this.is_active = is_active;
    }

}
