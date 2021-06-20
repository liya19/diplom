package ru.itis.diplom.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPLICATION_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PERSON_ID", nullable = false)
    private Person person;

    @JoinColumn(name = "DOCUMENT_KIND_ID")
    @ManyToOne(fetch = FetchType.EAGER)
    private DocumentKind documentKind;

    private Date date;

    @JoinTable(name = "CATEGORIES_APPLICATION",
            joinColumns = @JoinColumn(name = "application_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    @ManyToMany
    private List<Category> categories;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private Status status;

    @Column(name = "ISSUED_DATE")
    private Date issuedDate;

    public enum Status {
        ACCEPTED, ISSUED, SUGGESTED
    }

    public void setStatus(Status status) {
        this.status = status;
        if (status.equals(Status.ISSUED)) {
            this.issuedDate = new Date();
        }
    }
}
