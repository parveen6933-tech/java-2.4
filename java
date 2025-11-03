PART A — Connect to MySQL & Fetch Data (Employee Table)
 Database Table
CREATE DATABASE CompanyDB;
USE CompanyDB;

CREATE TABLE Employee (
    EmpID INT PRIMARY KEY,
    Name VARCHAR(50),
    Salary FLOAT
);

INSERT INTO Employee VALUES (101, 'Arjun', 35000);
INSERT INTO Employee VALUES (102, 'Neha', 40000);
INSERT INTO Employee VALUES (103, 'Rohit', 38000);

 FetchEmployee.java
import java.sql.*;

public class FetchEmployee {
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/CompanyDB";
        String user = "root";
        String pass = "root";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Employee");

            System.out.println("EmpID\tName\tSalary");

            while (rs.next()) {
                System.out.println(rs.getInt(1) + "\t" +
                        rs.getString(2) + "\t" +
                        rs.getFloat(3));
            }

            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}


 Demonstrates basic DB connectivity and reading results

 PART B — CRUD on Product Table Using JDBC + Transactions
 Product Table
CREATE DATABASE ShopDB;
USE ShopDB;

CREATE TABLE Product (
    ProductID INT PRIMARY KEY,
    ProductName VARCHAR(50),
    Price FLOAT,
    Quantity INT
);

 ProductCRUD.java (Menu-driven)
import java.sql.*;
import java.util.*;

public class ProductCRUD {

    static final String URL = "jdbc:mysql://localhost:3306/ShopDB";
    static final String USER = "root";
    static final String PASS = "root";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL, USER, PASS);
            con.setAutoCommit(false);

            while (true) {
                System.out.println("\n1. Add Product\n2. View Products\n3. Update Product\n4. Delete Product\n5. Exit");
                System.out.print("Enter choice: ");
                int ch = sc.nextInt();

                if (ch == 1) {
                    System.out.print("ProductID: ");
                    int id = sc.nextInt();
                    System.out.print("Product Name: ");
                    String name = sc.next();
                    System.out.print("Price: ");
                    float price = sc.nextFloat();
                    System.out.print("Quantity: ");
                    int qty = sc.nextInt();

                    PreparedStatement ps = con.prepareStatement("INSERT INTO Product VALUES (?, ?, ?, ?)");
                    ps.setInt(1, id);
                    ps.setString(2, name);
                    ps.setFloat(3, price);
                    ps.setInt(4, qty);
                    ps.executeUpdate();
                    con.commit();
                    System.out.println("Product Added!");

                } else if (ch == 2) {
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM Product");

                    System.out.println("ID\tName\tPrice\tQty");
                    while (rs.next()) {
                        System.out.println(rs.getInt(1) + "\t" +
                                rs.getString(2) + "\t" +
                                rs.getFloat(3) + "\t" +
                                rs.getInt(4));
                    }

                } else if (ch == 3) {
                    System.out.print("Enter ProductID to Update: ");
                    int id = sc.nextInt();
                    System.out.print("New Price: ");
                    float price = sc.nextFloat();

                    PreparedStatement ps = con.prepareStatement(
                            "UPDATE Product SET Price=? WHERE ProductID=?");
                    ps.setFloat(1, price);
                    ps.setInt(2, id);
                    ps.executeUpdate();
                    con.commit();
                    System.out.println("Product Updated!");

                } else if (ch == 4) {
                    System.out.print("ProductID to Delete: ");
                    int id = sc.nextInt();

                    PreparedStatement ps = con.prepareStatement(
                            "DELETE FROM Product WHERE ProductID=?");
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    con.commit();
                    System.out.println("Product Deleted!");

                } else {
                    break;
                }
            }
            con.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        sc.close();
    }
}


 Demonstrates:
 CRUD operations
 PreparedStatement
 Transaction handling (commit/rollback)

 PART C — Student Management — MVC Architecture + JDBC

 Student Table

CREATE DATABASE CollegeDB;
USE CollegeDB;

CREATE TABLE Student (
    StudentID INT PRIMARY KEY,
    Name VARCHAR(50),
    Department VARCHAR(20),
    Marks INT
);

 MODEL
 Student.java
public class Student {
    private int studentID;
    private String name;
    private String department;
    private int marks;

    public Student(int studentID, String name, String department, int marks) {
        this.studentID = studentID;
        this.name = name;
        this.department = department;
        this.marks = marks;
    }

    // Getter methods only for simplicity
    public int getStudentID() { return studentID; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public int getMarks() { return marks; }
}

 CONTROLLER (Database Operations)
 StudentDAO.java
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    private Connection connect() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/CollegeDB", "root", "root");
    }

    public void addStudent(Student s) throws Exception {
        Connection con = connect();
        PreparedStatement ps = con.prepareStatement("INSERT INTO Student VALUES (?, ?, ?, ?)");
        ps.setInt(1, s.getStudentID());
        ps.setString(2, s.getName());
        ps.setString(3, s.getDepartment());
        ps.setInt(4, s.getMarks());
        ps.executeUpdate();
        con.close();
    }

    public List<Student> getAllStudents() throws Exception {
        List<Student> list = new ArrayList<>();
        Connection con = connect();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM Student");

        while (rs.next()) {
            list.add(new Student(
                rs.getInt(1), rs.getString(2),
                rs.getString(3), rs.getInt(4)));
        }
        con.close();
        return list;
    }

    public void deleteStudent(int id) throws Exception {
        Connection con = connect();
        PreparedStatement ps = con.prepareStatement("DELETE FROM Student WHERE StudentID=?");
        ps.setInt(1, id);
        ps.executeUpdate();
        con.close();
    }
}

 VIEW (User Interface)
 StudentApp.java
import java.util.*;

public class StudentApp {
    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);
        StudentDAO dao = new StudentDAO();

        while (true) {
            System.out.println("\n1. Add Student\n2. View Students\n3. Delete Student\n4. Exit");
            System.out.print("Enter choice: ");
            int ch = sc.nextInt();

            if (ch == 1) {
                System.out.print("ID: ");
                int id = sc.nextInt();
                System.out.print("Name: ");
                String name = sc.next();
                System.out.print("Department: ");
                String dept = sc.next();
                System.out.print("Marks: ");
                int marks = sc.nextInt();

                Student s = new Student(id, name, dept, marks);
                dao.addStudent(s);
                System.out.println("Student Added!");

            } else if (ch == 2) {
                var list = dao.getAllStudents();
                System.out.println("ID\tName\tDept\tMarks");
                list.forEach(stu ->
                    System.out.println(stu.getStudentID() + "\t" +
                            stu.getName() + "\t" +
                            stu.getDepartment() + "\t" +
                            stu.getMarks()));

            } else if (ch == 3) {
                System.out.print("Enter StudentID to delete: ");
                dao.deleteStudent(sc.nextInt());
                System.out.println("Student Deleted!");

            } else {
                break;
            }
        }
        sc.close();
    }
}
