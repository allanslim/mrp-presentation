package com.codewarrior.csc686.project.presentation.model;

import org.apache.commons.lang3.StringUtils;

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


    private String email;


    private String password;

    private String question1;
    private String question2;
    private String question3;

    private String answer1;
    private String answer2;
    private String answer3;



    public boolean isEmailAndPasswordSupplied() {
        if(StringUtils.isBlank(email) || StringUtils.isBlank(password) ) {
            return false;
        }
        return true;
    }

    public boolean areQuestionsAndAnswersSupplied() {

        if (StringUtils.isBlank(question1) ||
                StringUtils.isBlank(question2) ||
                StringUtils.isBlank(question3) ||
                StringUtils.isBlank(answer1) ||
                StringUtils.isBlank(answer2) ||
                StringUtils.isBlank(answer3)) {
            return false;
        }
        return true;
    }


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

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public String getQuestion1() {

        return question1;
    }

    public void setQuestion1(String question1) {

        this.question1 = question1;
    }

    public String getQuestion2() {

        return question2;
    }

    public void setQuestion2(String question2) {

        this.question2 = question2;
    }

    public String getQuestion3() {

        return question3;
    }

    public void setQuestion3(String question3) {

        this.question3 = question3;
    }

    public String getAnswer1() {

        return answer1;
    }

    public void setAnswer1(String answer1) {

        this.answer1 = answer1;
    }

    public String getAnswer2() {

        return answer2;
    }

    public void setAnswer2(String answer2) {

        this.answer2 = answer2;
    }

    public String getAnswer3() {

        return answer3;
    }

    public void setAnswer3(String answer3) {

        this.answer3 = answer3;
    }
}
