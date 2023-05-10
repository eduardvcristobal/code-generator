package com.cict.core.base;


import com.cict.core.util.IdConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import static com.cict.core.util.IdConverter.fromId;

@SuppressWarnings("serial")
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseModel extends Loggable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "created_by", length = 50, updatable = false)
    @CreatedBy
    private String createdBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "time_created", updatable = false)
    @CreatedDate
    @CreationTimestamp
    private LocalDateTime timeCreated;

    @Column(name = "updated_by", length = 50)
    @LastModifiedBy
    private String updatedBy;
    @Column(name = "time_updated")

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @UpdateTimestamp
    @LastModifiedDate
    private LocalDateTime timeUpdated;

    @Setter
    @Column(name = "active")
    private Boolean active = Boolean.TRUE;

    @Transient
    private String code;

    @Transient
    private String description;

//    @JoinColumn(name = "sbu_id")
//    @Column(name = "active")
//    private long sbuId = 1;

    @Column(name = "sbu_id")
    private long sbuId;

    private boolean isActive() {
        if(this.active == null)
            return false;
        else
            return this.active;
    }

    public String getStringId() {
        return fromId(this.id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setId(String id) {
        //TODO: this is a bit lazy, it might cause an error on tha last line of code on catch
        // pansamantagal muna.
        if(id == null || id.isEmpty()) { this.id = null; }
        else {
            try {
                this.id = Long.parseLong(id);
            } catch (Exception e) {
                this.id = IdConverter.toId(id);
            }
        }
    }


}
