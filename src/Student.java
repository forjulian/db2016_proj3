

/**
 * Created by Evgenii_ on 4/28/2016.
 */
public class Student {

    int id;
    String name;
    String dept_name;
    int tot_cred;

   public Student(int id, String name){
       this.id = id;
       this.name = name;
       this.dept_name = dept_name;
       this.tot_cred = tot_cred;
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
        return this.tot_cred;
    }

    @Override
    public String toString(){
        return "Id: "+ id + "\tName: " + name + "\t Dept_name: " + dept_name + "\t Tot_Cred: " + tot_cred + "\n\n";
    }

}
