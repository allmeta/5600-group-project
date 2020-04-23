package asmund.thomas.group_project;


public class Person {

    String id;
    String firstName;
    String lastName;
    String passClear;
    String passHash;
    String email;
    int numberOfClaims;

    public Person() {
        id = "na";
        firstName = "na";
        lastName = "na";
        passClear = "na";
        passHash = "na";
        email = "na";
        numberOfClaims = 0;
    }

    public String getId(){return this.id;}

    public String getFirstName(){return this.firstName;}

    public String getLastName(){return this.lastName;}

    public String getPassClear(){return this.passClear;}

    public String getPassHash(){return this.passHash;}

    public String getEmail(){return this.email;}

    public int getNumberOfClaims() {return numberOfClaims;}

    public void setNumberOfClaims(int numberOfClaims) {
        this.numberOfClaims = numberOfClaims;
    }

    public void addClaim() {
        numberOfClaims++;
    }
}


