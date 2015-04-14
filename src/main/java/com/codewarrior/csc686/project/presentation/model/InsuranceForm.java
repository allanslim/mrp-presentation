package com.codewarrior.csc686.project.presentation.model;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class InsuranceForm {

    @NotNull
    @Digits(integer=50, fraction=2)
    private Integer groupId;

    @NotNull
    private String memberId;

    @NotNull
    private String firstname;

    @NotNull
    private String lastname;

    @NotNull
    private Date birthDate;

    public Integer getGroupId() {

        return groupId;
    }

    public void setGroupId(Integer groupId) {

        this.groupId = groupId;
    }

    public String getMemberId() {

        return memberId;
    }

    public void setMemberId(String memberId) {

        this.memberId = memberId;
    }

    public String getFirstname() {

        return firstname;
    }

    public void setFirstname(String firstname) {

        this.firstname = firstname;
    }

    public String getLastname() {

        return lastname;
    }

    public void setLastname(String lastname) {

        this.lastname = lastname;
    }

    public Date getBirthDate() {

        return birthDate;
    }

    public void setBirthDate(Date birthDate) {

        this.birthDate = birthDate;
    }
}
