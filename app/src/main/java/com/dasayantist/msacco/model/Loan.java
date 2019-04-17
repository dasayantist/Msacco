package com.dasayantist.msacco.model;

public class Loan {
    private String id;
    private String loan_type;
    private String loan_id;
    private String loan_amount;
    private String principal;
    private String interest;
    private String s_time;
    private String penalty;

    public Loan(String loan_type, String loan_id, String loan_amount, String principal, String interest, String s_time, String penalty) {
        this.loan_type = loan_type;
        this.loan_id = loan_id;
        this.loan_amount = loan_amount;
        this.principal = principal;
        this.interest = interest;
        this.s_time = s_time;
        this.penalty = penalty;
    }

    public Loan(String id, String loan_type, String loan_id, String loan_amount, String principal, String interest, String s_time, String penalty) {
        this.id = id;
        this.loan_type = loan_type;
        this.loan_id = loan_id;
        this.loan_amount = loan_amount;
        this.principal = principal;
        this.interest = interest;
        this.s_time = s_time;
        this.penalty = penalty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //loan_type, loan_id, loan_amount, principal, interest, s_time,penalty
    public String getLoan_type() {
        return loan_type;
    }

    public void setLoan_type(String loan_type) {
        this.loan_type = loan_type;
    }

    public String getLoan_id() {
        return loan_id;
    }

    public void setLoan_id(String loan_id) {
        this.loan_id = loan_id;
    }

    public String getLoan_amount() {
        return loan_amount;
    }

    public void setLoan_amount(String loan_amount) {
        this.loan_amount = loan_amount;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getS_time() {
        return s_time;
    }

    public void setS_time(String s_time) {
        this.s_time = s_time;
    }

    public String getPenalty() {
        return penalty;
    }

    public void setPenalty(String penalty) {
        this.penalty = penalty;
    }



    public Loan() {
    }

}

