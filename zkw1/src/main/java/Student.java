public class Student {
    public int age;
    public String name;

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }


    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "kk{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}
