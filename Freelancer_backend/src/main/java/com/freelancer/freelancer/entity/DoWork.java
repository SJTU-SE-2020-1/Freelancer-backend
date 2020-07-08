package com.freelancer.freelancer.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import java.sql.Timestamp;

import javax.persistence.*;

@Data
@Entity
@IdClass(DoWorkPK.class)
@Table(name = "doWork")
@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer","fieldHandler"})
public class DoWork {

    @Id
    private int uId;

    @Id
    private int wId;

    private Timestamp startTime;
    private Timestamp endTime;
    private Double payment;
    private String review;

}