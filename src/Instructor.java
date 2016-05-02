

/**
 * Created by Evgenii_ on 4/30/2016.
 */
public class Instructor {
    int id;
    String name;
    String dept_name;
    int salary;

    public Instructor(int id, String name){
        this.id = id;
        this.name = name;
        this.dept_name = dept_name;
        this.salary = salary;
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String dept_name(){
        return this.dept_name;
    }

    public int getTot_cred(){
        return this.salary;
    }

    @Override
    public String toString(){
        return "Id: "+ id + "\tName: " + name + "\t Dept_name: " + dept_name + "\t salary: " + salary + "\n\n";
    }
}
