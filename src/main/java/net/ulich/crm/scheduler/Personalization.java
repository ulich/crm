package net.ulich.crm.scheduler;

public class Personalization {
    private final String companyName;
    private final String salutation;
    private final String firstName;
    private final String lastName;
    private final String street;
    private final String postCode;
    private final String city;

    public Personalization(String companyName, String salutation, String firstName, String lastName, String street, String postCode, String city) {
        this.companyName = companyName;
        this.salutation = salutation;
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.postCode = postCode;
        this.city = city;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getSalutation() {
        return salutation;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getStreet() {
        return street;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getCity() {
        return city;
    }
}
