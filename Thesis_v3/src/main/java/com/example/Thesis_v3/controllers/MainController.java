package com.example.Thesis_v3.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import com.example.Thesis_v3.models.*;

import javax.persistence.criteria.CriteriaBuilder;


@Controller
public class MainController {

    private String url = "jdbc:mysql://localhost:3307/diploma";
    private String username = "root";
    private String passDB = "root";
    private String loginUser;
    private String loginPass;
    private int idProjBuf;
    private int idComandBuf;
    private List<Tasks> bufTasksListWeek;
    private List<Tasks> bufTasksListMonth;
    private List<Tasks> bufTasksListSixMonth;
    private Projects bufProject;

    @GetMapping("/")
    public String autorization(Model model) {
        model.addAttribute("title", "Авторизация");
        return "home";
    }

    @PostMapping("/")
    public String autorization(@RequestParam String emailName, @RequestParam String password, Model model) {
        try {
            Connection connection = DriverManager.getConnection(url, username, passDB);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from users where email ='" + emailName + "'");
            if (resultSet.next()) {
                if (resultSet.getString("password").equals(password)) {
                    loginUser = resultSet.getString("login");
                    loginPass = resultSet.getString("password");
                    if (resultSet.getString("isActived").equals("1")) {
                        if (resultSet.getString("isAdmin").equals("1")) {
                            return "workzoneAdmin";
                        } else {
                            return "workzoneUser";
                        }
                    } else {
                        System.out.println("Error#Account is not activated, please talk with admin#");
                        return "redirect:/";
                    }
                } else {
                    System.out.println("Error#Incorrect password#");
                    return "redirect:/";
                }
            } else {
                System.out.println("Error#This user is not exist#");
                return "redirect:/";

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @GetMapping("/register")
    public String register(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String email,
                           @RequestParam String name,
                           @RequestParam String surname,
                           @RequestParam String login,
                           @RequestParam(value = "telephoneCode") String telephoneCode,
                           @RequestParam String telephone,
                           @RequestParam String password) {
        Users user = new Users(0, name, surname, login, password, telephone, email, 0, 0);
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from users where email ='" + email + "'");
            while (resultSet.next()) {
                if (resultSet.getString("email").equals(email)) {
                    System.out.println("Error#Этот пользователь уже есть(incorrect email");
                }
                if (resultSet.getString("login").equals(login)) {
                    System.out.println("Error#Этот пользователь уже есть(incorrect login");
                }
            }

            statement.executeUpdate("insert into users(Name,Surname,login,password,telephone,email,isAdmin,isActived) " +
                    "values('" + name + "','" + surname + "','" + login + "','" + password + "','" + telephone + "','" + email + "'," + "false,false)");
            return "redirect:/";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/register";
    }

    @GetMapping("/controlComands")
    public String controlComands(Model model) {
        List<Commands> commandsList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from commands");
            while (resultSet.next()) {
                commandsList.add(new Commands(resultSet.getInt("ID"),
                        Integer.parseInt(resultSet.getString("ID_projects")),
                        resultSet.getString("ID_users"),
                        Integer.parseInt(resultSet.getString("ID_kurator")),
                        Integer.parseInt(resultSet.getString("ID_manager")),
                        Integer.parseInt(resultSet.getString("ID_customer")),
                        resultSet.getString("NameCommand")));
            }
            model.addAttribute("comands", commandsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "controlComands";
    }

    @GetMapping("/controlProjects")
    public String controlProjects(Model model) {
        List<Projects> projectsList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from projects");
            while (resultSet.next()) {
                projectsList.add(new Projects(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("ID_tasks")));
            }
            model.addAttribute("projects", projectsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "controlProjects";
    }

    @GetMapping("/createTheme")
    public String createTheme(Model model) {
        return "createTheme";
    }

    @PostMapping("/createTheme")
    public String createTheme(@RequestParam String name, @RequestParam String content, Model model) {
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            SimpleDateFormat dateFormat = new SimpleDateFormat("E dd.MM.yyyy hh:mm:ss k zzz");
            Calendar date = new GregorianCalendar();
            statement.executeUpdate("insert into supportthemes (nametheme, author, creatingdate, content) " +
                    "VALUES ('" + name + "','" + loginUser + "','" + date.get(Calendar.YEAR) + "." + date.get(Calendar.MONTH) + "." + date.get(Calendar.DAY_OF_MONTH) + "','" + content + "')");
            return "redirect:/support";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/createTheme";
    }

    @GetMapping("/listuser")
    public String listUser(Model model) {
        List<Users> usersList = new ArrayList<>();
        List<Curators> curatorsList = new ArrayList<>();
        List<Customers> customersList = new ArrayList<>();
        List<Manager> managerList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from users");
            while (resultSet.next()) {
                usersList.add(new Users(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getString("telephone"),
                        resultSet.getString("email"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived")));
            }
            model.addAttribute("users", usersList);
            resultSet = statement.executeQuery("select * from curators");
            while (resultSet.next()) {
                curatorsList.add(new Curators(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived"),
                        resultSet.getString("telephone"),
                        resultSet.getString("email"),
                        resultSet.getString("idCommand")));
            }
            model.addAttribute("curators", curatorsList);
            resultSet = statement.executeQuery("select * from customers");
            while (resultSet.next()) {
                customersList.add(new Customers(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived"),
                        resultSet.getString("email"),
                        resultSet.getString("idCommand"),
                        resultSet.getString("telephone")));
            }
            model.addAttribute("customers", customersList);
            resultSet = statement.executeQuery("select * from managers");
            while (resultSet.next()) {
                managerList.add(new Manager(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived"),
                        resultSet.getString("telephone"),
                        resultSet.getString("email"),
                        resultSet.getString("idCommand")));
            }
            model.addAttribute("managers", managerList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "listuser";

    }

    @PostMapping("/listuser")
    public String listuser(@RequestParam(value = "verUs", required = false) String verUs,
                           @RequestParam(value = "actUs", required = false) String actUs,
                           @RequestParam(value = "confirmUs", required = false) String confirmUs,
                           @RequestParam(value = "verCur", required = false) String verCur,
                           @RequestParam(value = "actCur", required = false) String actCur,
                           @RequestParam(value = "confirmCur", required = false) String confirmCur,
                           @RequestParam(value = "verCus", required = false) String verCus,
                           @RequestParam(value = "actCus", required = false) String actCus,
                           @RequestParam(value = "confirmCus", required = false) String confirmCus,
                           @RequestParam(value = "verMan", required = false) String verMan,
                           @RequestParam(value = "actMan", required = false) String actMan,
                           @RequestParam(value = "confirmMan", required = false) String confirmMan) {
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            System.out.println(confirmCur);
            if (actUs != null) {
                statement.executeUpdate("update users set isAdmin= '" + 1 + "' where login ='" + confirmUs + "'");
            } else {
                statement.executeUpdate("update users set isAdmin= '" + 0 + "' where login ='" + confirmUs + "'");
            }
            if (verUs != null) {
                statement.executeUpdate("update users set isActived = '" + 1 + "' where login ='" + confirmUs + "'");
            } else {
                statement.executeUpdate("update users set isActived = '" + 0 + "' where login ='" + confirmUs + "'");
            }
            if (actCur != null) {
                statement.executeUpdate("update curators set isAdmin= '" + 1 + "' where login ='" + confirmCur + "'");
            } else {
                statement.executeUpdate("update curators set isAdmin= '" + 0 + "' where login ='" + confirmCur + "'");
            }
            if (verCur != null) {
                statement.executeUpdate("update curators set isActived = '" + 1 + "' where login ='" + confirmCur + "'");
            } else {
                statement.executeUpdate("update curators set isActived = '" + 0 + "' where login ='" + confirmCur + "'");
            }
            if (actCus != null) {
                statement.executeUpdate("update customers set isAdmin= '" + 1 + "' where login ='" + confirmCus + "'");
            } else {
                statement.executeUpdate("update customers set isAdmin= '" + 0 + "' where login ='" + confirmCus + "'");
            }
            if (verCus != null) {
                statement.executeUpdate("update customers set isActived = '" + 1 + "' where login ='" + confirmCus + "'");
            } else {
                statement.executeUpdate("update customers set isActived = '" + 0 + "' where login ='" + confirmCus + "'");
            }
            if (actMan != null) {
                statement.executeUpdate("update managers set isAdmin= '" + 1 + "' where login ='" + confirmMan + "'");
            } else {
                statement.executeUpdate("update managers set isAdmin= '" + 0 + "' where login ='" + confirmMan + "'");
            }
            if (verMan != null) {
                statement.executeUpdate("update managers set isActived = '" + 1 + "' where login ='" + confirmMan + "'");
            } else {
                statement.executeUpdate("update managers set isActived = '" + 0 + "' where login ='" + confirmMan + "'");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/listuser";
    }

    @GetMapping("/personalAccountUser")
    public String personalAccountUser(Model model) {
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Users users;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from users where login = '" + loginUser + "'");
            if (resultSet.next()) {
                users = new Users(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getString("telephone"),
                        resultSet.getString("email"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived"));

                model.addAttribute("name", users.getName());
                model.addAttribute("surname", users.getSurname());
                model.addAttribute("login", users.getLogin());
                model.addAttribute("email", users.getEmail());
                model.addAttribute("telephone", users.getTelephone());
                model.addAttribute("isadmin", users.getIsAdmin());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "personalAccountUser";
    }

    @GetMapping("/personalAccountCurators")
    public String personalAccountCurators(Model model) {
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Curators curators;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select from curators where login ='" + loginUser + "'");
            curators = new Curators(resultSet.getInt("ID"),
                    resultSet.getString("Name"),
                    resultSet.getString("Surname"),
                    resultSet.getString("login"),
                    resultSet.getString("password"),
                    resultSet.getInt("isAdmin"),
                    resultSet.getInt("isActived"),
                    resultSet.getString("telephone"),
                    resultSet.getString("email"),
                    resultSet.getString("idCommand"));
            resultSet = statement.executeQuery("select from projects where ID ='" + curators.getIdCommand() + "'");
            Commands commands = new Commands(resultSet.getInt("ID"),
                    resultSet.getInt("ID_projects"),
                    resultSet.getString("ID_users"),
                    resultSet.getInt("ID_kurator"),
                    resultSet.getInt("ID_manager"),
                    resultSet.getInt("ID_customer"),
                    resultSet.getString("NameCommand"));
            model.addAttribute("name", curators.getName());
            model.addAttribute("surname", curators.getSurname());
            model.addAttribute("login", curators.getLogin());
            model.addAttribute("email", curators.getEmail());
            model.addAttribute("telephone", curators.getTelephone());
            model.addAttribute("isadmin", curators.getIsAdmin());
            model.addAttribute("command", commands);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "personalAccountCurators";
    }

    @GetMapping("/personalAccountCustomer")
    public String personalAccountCustomer(Model model) {
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Customers customers;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select from curators where login ='" + loginUser + "'");
            customers = new Customers(resultSet.getInt("ID"),
                    resultSet.getString("Name"),
                    resultSet.getString("Surname"),
                    resultSet.getString("login"),
                    resultSet.getString("password"),
                    resultSet.getInt("isAdmin"),
                    resultSet.getInt("isActived"),
                    resultSet.getString("email"),
                    resultSet.getString("idCommand"),
                    resultSet.getString("telephone"));
            resultSet = statement.executeQuery("select from projects where ID ='" + customers.getIdCommand() + "'");
            Commands commands = new Commands(resultSet.getInt("ID"),
                    resultSet.getInt("ID_projects"),
                    resultSet.getString("ID_users"),
                    resultSet.getInt("ID_kurator"),
                    resultSet.getInt("ID_manager"),
                    resultSet.getInt("ID_customer"),
                    resultSet.getString("NameCommand"));
            model.addAttribute("name", customers.getName());
            model.addAttribute("surname", customers.getSurname());
            model.addAttribute("login", customers.getLogin());
            model.addAttribute("email", customers.getEmail());
            model.addAttribute("telephone", customers.getTelephone());
            model.addAttribute("isadmin", customers.getIsAdmin());
            model.addAttribute("command", commands);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "personalAccountCustomer";
    }

    @GetMapping("/personalAccountManager")
    public String personalAccountManager(Model model) {
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Manager manager;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select from curators where login ='" + loginUser + "'");
            manager = new Manager(resultSet.getInt("ID"),
                    resultSet.getString("Name"),
                    resultSet.getString("Surname"),
                    resultSet.getString("login"),
                    resultSet.getString("password"),
                    resultSet.getInt("isAdmin"),
                    resultSet.getInt("isActived"),
                    resultSet.getString("telephone"),
                    resultSet.getString("email"),
                    resultSet.getString("idCommand"));
            resultSet = statement.executeQuery("select from projects where ID ='" + manager.getIdCommand() + "'");
            Commands commands = new Commands(resultSet.getInt("ID"),
                    resultSet.getInt("ID_projects"),
                    resultSet.getString("ID_users"),
                    resultSet.getInt("ID_kurator"),
                    resultSet.getInt("ID_manager"),
                    resultSet.getInt("ID_customer"),
                    resultSet.getString("NameCommand"));
            model.addAttribute("name", manager.getName());
            model.addAttribute("surname", manager.getSurname());
            model.addAttribute("login", manager.getLogin());
            model.addAttribute("email", manager.getEmail());
            model.addAttribute("telephone", manager.getTelephone());
            model.addAttribute("isadmin", manager.getIsAdmin());
            model.addAttribute("command", commands);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "personalAccountManager";
    }

    @GetMapping("/support")
    public String support(Model model) {
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            List<Theme> themeList = new ArrayList<>();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from supportthemes");
            while (resultSet.next()) {
                themeList.add(new Theme(resultSet.getInt("ID"),
                        resultSet.getString("nametheme"),
                        resultSet.getString("author"),
                        resultSet.getDate("creatingdate"),
                        resultSet.getString("content")));
            }
            model.addAttribute("themes", themeList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/support";
    }

    @GetMapping("/workzoneAdmin")
    public String workzoneAdmin(Model model) {
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from users where login='" + loginUser + "'");
            if (resultSet.next()) {
                Users users = new Users(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getString("telephone"),
                        resultSet.getString("email"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived"));
                model.addAttribute("login", loginUser);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "workzoneAdmin";
    }

    @GetMapping("/workzoneUser")
    public String workzoneUser(Model model) {
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from users where login='" + loginUser + "'");
            if (resultSet.next()) {
                Users users = new Users(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getString("telephone"),
                        resultSet.getString("email"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived"));
                model.addAttribute("login", users.getLogin());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "workzoneUser";
    }

    @GetMapping("/addComand")
    public String addComand(Model model) {
        List<Curators> curatorsList = new ArrayList<>();
        List<Customers> customersList = new ArrayList<>();
        List<Manager> managerList = new ArrayList<>();
        List<Users> usersList = new ArrayList<>();
        List<Projects> projectsList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from curators");

            while (resultSet.next()) {
                curatorsList.add(new Curators(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived"),
                        resultSet.getString("telephone"),
                        resultSet.getString("email"),
                        resultSet.getString("idCommand")));
            }
            resultSet = statement.executeQuery("select * from customers");
            while (resultSet.next()) {
                customersList.add(new Customers(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived"),
                        resultSet.getString("email"),
                        resultSet.getString("idCommand"),
                        resultSet.getString("telephone")));
            }
            resultSet = statement.executeQuery("select * from managers");
            while (resultSet.next()) {
                managerList.add(new Manager(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived"),
                        resultSet.getString("telephone"),
                        resultSet.getString("email"),
                        resultSet.getString("idCommand")));
            }
            resultSet = statement.executeQuery("select * from projects");
            while (resultSet.next()) {
                projectsList.add(new Projects(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("ID_tasks")));
            }
            resultSet = statement.executeQuery("select * from users");
            while (resultSet.next()) {
                usersList.add(new Users(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getString("telephone"),
                        resultSet.getString("email"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived")));
            }
            model.addAttribute("projects", projectsList);
            model.addAttribute("curators", curatorsList);
            model.addAttribute("managers", managerList);
            model.addAttribute("customers", customersList);
            model.addAttribute("users", usersList);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "addComand";
    }

    @PostMapping("/addComand")
    public String addComand(@RequestParam String name, @RequestParam(value = "proj") String project, @RequestParam(value = "cur") String curator, @RequestParam(value = "man") String manager, @RequestParam(value = "cus") String customer, @RequestParam(value = "use") String user, Model model) {
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from curators where ID ='" + curator + "'");
            Curators curators = new Curators();
            if (resultSet.next()) {
                curators = new Curators(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived"),
                        resultSet.getString("telephone"),
                        resultSet.getString("email"),
                        resultSet.getString("idCommand"));
            }
            resultSet = statement.executeQuery("select * from managers where ID ='" + manager + "'");
            Manager manager1 = new Manager();
            if (resultSet.next()) {
                manager1 = new Manager(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived"),
                        resultSet.getString("telephone"),
                        resultSet.getString("email"),
                        resultSet.getString("idCommand"));
            }
            resultSet = statement.executeQuery("select * from customers where ID ='" + customer + "'");
            Customers customers = new Customers();
            if (resultSet.next()) {
                customers = new Customers(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived"),
                        resultSet.getString("email"),
                        resultSet.getString("idCommand"),
                        resultSet.getString("telephone"));
            }
            resultSet = statement.executeQuery("select * from users where ID ='" + user + "'");
            Users users = new Users();
            if (resultSet.next()) {
                users = new Users(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getString("telephone"),
                        resultSet.getString("email"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived"));
            }
            resultSet = statement.executeQuery("select * from projects where ID ='" + project + "'");
            Projects projects = new Projects();
            if (resultSet.next()) {
                projects = new Projects(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("ID_tasks"));
            }
            statement.executeUpdate("insert into commands (ID_projects, ID_users, ID_kurator, ID_manager, ID_customer, NameCommand) " +
                    "values ('" + projects.getID() + "','" + users.getID() + "','" + curators.getID() + "','" + manager1.getID() + "','" + customers.getID() + "','" + name + "')");
            return "redirect:/controlComands";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/addComand";
    }

    @GetMapping("/addProject")
    public String addProject(Model model) {
        return "addProject";
    }

    @PostMapping("/addProject")
    public String addProject(@RequestParam String name, Model model) {
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            List<Projects> projectsList = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery("select * from projects where Name = '" + name + "'");
            if (resultSet.next()) {
                System.out.println("Error#Этот проект уже существует");
                return "redirect:/addProject";
            } else {
                statement.executeUpdate("insert into projects (Name) values('" + name + "')");
                return "redirect:/controlProjects";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/addProject";
    }

    @GetMapping("/addTask")
    public String addTask(Model model) {
        return "addTask";
    }

    @PostMapping("/addTask")
    public String addTask(@RequestParam String name, @RequestParam String finishDate, Model model) {
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            Calendar startDate = new GregorianCalendar();
            System.out.println(startDate.getTime());
            statement.executeUpdate("insert into tasks (Name, startDate, finishDate, completed) " +
                    "values ('" + name + "','" + startDate.get(Calendar.DAY_OF_MONTH) + "." + startDate.get(Calendar.MONTH) + "." + startDate.get(Calendar.YEAR) + "','" + finishDate + "','0')");
            return "redirect:/controlProjects";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/addTask";
    }

    @GetMapping("/deleteComand")
    public String deleteComand(Model model) {
        return "deleteComand";
    }

    @PostMapping("/deleteComand")
    public String deleteComand(@RequestParam String ID, Model model) {
        int id = Integer.parseInt(ID);
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from commands where ID='" + id + "'");
            if (!resultSet.next()) {
                System.out.println("Error#Такой команды не существует");
                return "redirect:/deleteComand";
            } else {
                statement.executeUpdate("delete from commands where ID='" + id + "'");
                return "redirect:/controlComands";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/deleteComand";
    }

    @GetMapping("/deleteProject")
    public String deleteProjects(Model model) {
        return "deleteProject";
    }

    @PostMapping("/deleteProject")
    public String deleteProjects(@RequestParam String ID, Model model) {
        int id = Integer.parseInt(ID);
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from projects where ID='" + id + "'");
            if (!resultSet.next()) {
                System.out.println("Error#Такой команды не существует");
                return "redirect:/deleteProject";
            } else {
                statement.executeUpdate("delete from projects where ID='" + id + "'");
                return "redirect:/controlProjects";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/deleteProject";
    }

    @GetMapping("/deleteTask")
    public String deleteTask(Model model) {
        return "/deleteTask";
    }

    @PostMapping("/deleteTask")
    public String deleteTask(@RequestParam String ID, Model model) {
        int id = Integer.parseInt(ID);
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from tasks where ID='" + id + "'");
            if (!resultSet.next()) {
                System.out.println("Error#Такой задачи не существует");
                return "redirect:/deleteTask";
            } else {
                statement.executeUpdate("delete from tasks where ID='" + id + "'");
                return "redirect:/controlProject";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/deleteTask";
    }

    @GetMapping("/showProject/{IDproj}")
    public String showProject(@PathVariable(value = "IDproj") String IDproj, Model model) {
        List<Tasks> tasksList = new ArrayList<>();
        Projects projects = new Projects();
        Commands commands = new Commands();
        Curators curators = new Curators();
        Manager manager = new Manager();
        Customers customers = new Customers();
        List<Users> usersList = new ArrayList<>();
        String[] IdTasksArr = new String[0];
        int numberWeek = 0;
        int numberMonth = 0;
        int numberSixMonth = 0;

        idProjBuf = Integer.parseInt(IDproj);
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from projects where ID='" + idProjBuf + "'");
            if (resultSet.next()) {
                projects = new Projects(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("ID_tasks"));
            }
            if (!projects.getID_tasks().equals("")) {
                IdTasksArr = projects.getID_tasks().split("#");
                for (int i = 0; i < IdTasksArr.length; i++) {
                    resultSet = statement.executeQuery("select * from tasks where ID='" + IdTasksArr[i] + "'");
                    if (resultSet.next()) {
                        tasksList.add(new Tasks(resultSet.getInt("ID"),
                                resultSet.getString("Name"),
                                resultSet.getDate("startDate"),
                                resultSet.getDate("finishDate"),
                                resultSet.getString("IDcategories"),
                                resultSet.getInt("completed"),
                                resultSet.getDate("completedDate")));
                    }
                }
            }

            resultSet = statement.executeQuery("select * from commands where ID_projects='" + idProjBuf + "'");
            if (resultSet.next()) {
                commands = new Commands(resultSet.getInt("ID"),
                        Integer.parseInt(resultSet.getString("ID_projects")),
                        resultSet.getString("ID_users"),
                        Integer.parseInt(resultSet.getString("ID_kurator")),
                        Integer.parseInt(resultSet.getString("ID_manager")),
                        Integer.parseInt(resultSet.getString("ID_customer")),
                        resultSet.getString("NameCommand"));
            }
            resultSet = statement.executeQuery("select * from curators where ID='" + commands.getID_kurator() + "'");
            if (resultSet.next()) {
                curators = new Curators(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived"),
                        resultSet.getString("telephone"),
                        resultSet.getString("email"),
                        resultSet.getString("idCommand"));
            }
            resultSet = statement.executeQuery("select * from customers where ID='" + commands.getID_customer() + "'");
            if (resultSet.next()) {
                customers = new Customers(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived"),
                        resultSet.getString("email"),
                        resultSet.getString("idCommand"),
                        resultSet.getString("telephone"));
            }
            resultSet = statement.executeQuery("select * from managers where ID='" + commands.getID_manager() + "'");
            if (resultSet.next()) {
                manager = new Manager(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived"),
                        resultSet.getString("telephone"),
                        resultSet.getString("email"),
                        resultSet.getString("idCommand"));
            }
            System.out.println("Commands.getID_users() = " + commands.getID_users());
            if (!commands.getID_users().equals("")) {

                String[] idUsers = commands.getID_users().split("#");

                for (int i = 0; i < idUsers.length; i++) {
                    resultSet = statement.executeQuery("select * from users where ID='" + idUsers[i] + "'");
                    if (resultSet.next()) {
                        usersList.add(new Users(resultSet.getInt("ID"),
                                resultSet.getString("Name"),
                                resultSet.getString("Surname"),
                                resultSet.getString("login"),
                                resultSet.getString("password"),
                                resultSet.getString("telephone"),
                                resultSet.getString("email"),
                                resultSet.getInt("isAdmin"),
                                resultSet.getInt("isActived")));
                    }
                }
            }
            for (Users obj : usersList) {
                System.out.println(obj.toString());
            }
            for (int i = 0; i < IdTasksArr.length; i++) {
                resultSet = statement.executeQuery("select * from tasks where ID ='" + IdTasksArr[i] + "' " +
                        "and completed = '1' " +
                        "and completedDate >= DATE_ADD(CURRENT_DATE(), INTERVAL -7 DAY)");
                if (resultSet.next()) {
                    System.out.println(resultSet.getInt("ID") + "#" + resultSet.getString("Name") + "\n");
                    numberWeek++;
                }

            }
            for (int i = 0; i < IdTasksArr.length; i++) {
                resultSet = statement.executeQuery("select * from tasks where id ='" + IdTasksArr[i] + "' " +
                        "and completed = '1' " +
                        "and completedDate >= DATE_ADD(CURRENT_DATE(), INTERVAL -1 MONTH)");
                if (resultSet.next()) {
                    numberMonth++;
                }
            }
            for (int i = 0; i < IdTasksArr.length; i++) {
                resultSet = statement.executeQuery("select * from tasks where id ='" + IdTasksArr[i] + "' " +
                        "and completed = '1' " +
                        "and completedDate >= DATE_ADD(CURRENT_DATE(), INTERVAL -6 MONTH)");
                if (resultSet.next()) {
                    numberSixMonth++;
                }
            }
            System.out.println(numberWeek + "#" + numberMonth + "#" + numberSixMonth);
            model.addAttribute("tasks", tasksList);
            model.addAttribute("weekCompleted", numberWeek);
            model.addAttribute("monthCompleted", numberMonth);
            model.addAttribute("sixMonthCompleted", numberSixMonth);
            model.addAttribute("managerName", manager.getName());
            model.addAttribute("managerSurname", manager.getSurname());
            model.addAttribute("managerTelephone", manager.getTelephone());
            model.addAttribute("curatorName", curators.getName());
            model.addAttribute("curatorSurname", curators.getSurname());
            model.addAttribute("curatorTelephone", curators.getTelephone());
            model.addAttribute("customerName", customers.getName());
            model.addAttribute("customerSurname", customers.getSurname());
            model.addAttribute("customerTelephone", customers.getTelephone());
            model.addAttribute("users", usersList);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "showProject";
    }


    @PostMapping("/showProject")
    public String changeTaskStatus(@RequestParam(value = "confirm", required = false) String confirm, Model model){
        try(Connection connection = DriverManager.getConnection(url,username,passDB)){
            Statement statement = connection.createStatement();
            Calendar date = new GregorianCalendar();
            if(confirm!=null){
                statement.executeUpdate("update tasks set completed ='1', completedDate ='"+date.get(Calendar.DAY_OF_MONTH) + "." + date.get(Calendar.MONTH) + "." + date.get(Calendar.YEAR)+"' " +
                        "where id ='"+confirm+"'");
            }
            else{
                statement.executeUpdate("update tasks set completed ='0', completedDate ='null' " +
                        "where id ='"+confirm+"'");
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/controlProjects";
    }

    @GetMapping("/someComands/{IDcomand}")
    public String someComands(@PathVariable(value = "IDcomand") String IDcomand, Model model) {
        String[] tasksInfo;
        int numberWeek = 0;
        int numberMonth = 0;
        int numberSixMonth = 0;
        Projects projects = new Projects();
        Commands commands = new Commands();
        Curators curators = new Curators();
        Manager manager = new Manager();
        Customers customers = new Customers();
        List<Users> usersList = new ArrayList<>();
        //idProjBuf = Integer.parseInt(IDproj);
        idComandBuf = Integer.parseInt(IDcomand);
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();


            ResultSet resultSet = statement.executeQuery("select * from commands where ID='" + idComandBuf + "'");
            if (resultSet.next()) {
                commands = new Commands(resultSet.getInt("ID"),
                        Integer.parseInt(resultSet.getString("ID_projects")),
                        resultSet.getString("ID_users"),
                        Integer.parseInt(resultSet.getString("ID_kurator")),
                        Integer.parseInt(resultSet.getString("ID_manager")),
                        Integer.parseInt(resultSet.getString("ID_customer")),
                        resultSet.getString("NameCommand"));
            }
            resultSet = statement.executeQuery("select * from projects where id ='" + commands.getID_projects() + "'");
            if (resultSet.next()) {
                projects = new Projects(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("ID_tasks"));
            }
            resultSet = statement.executeQuery("select * from curators where ID='" + commands.getID_kurator() + "'");
            if (resultSet.next()) {
                curators = new Curators(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived"),
                        resultSet.getString("telephone"),
                        resultSet.getString("email"),
                        resultSet.getString("idCommand"));
            }
            resultSet = statement.executeQuery("select * from customers where ID='" + commands.getID_customer() + "'");
            if (resultSet.next()) {
                customers = new Customers(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived"),
                        resultSet.getString("email"),
                        resultSet.getString("idCommand"),
                        resultSet.getString("telephone"));
            }
            resultSet = statement.executeQuery("select * from managers where ID='" + commands.getID_manager() + "'");
            if (resultSet.next()) {
                manager = new Manager(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getInt("isAdmin"),
                        resultSet.getInt("isActived"),
                        resultSet.getString("telephone"),
                        resultSet.getString("email"),
                        resultSet.getString("idCommand"));
            }
            System.out.println("Commands: ID_users = " + commands.getID_users() + "ID = " + commands.getID() + "ID_projects = " + commands.getID_projects());
            System.out.println(commands.toString());
            String[] idUsers = commands.getID_users().split("#");

            for (int i = 0; i < idUsers.length; i++) {
                resultSet = statement.executeQuery("select * from users where ID='" + idUsers[i] + "'");
                if (resultSet.next()) {
                    usersList.add(new Users(resultSet.getInt("ID"),
                            resultSet.getString("Name"),
                            resultSet.getString("Surname"),
                            resultSet.getString("login"),
                            resultSet.getString("password"),
                            resultSet.getString("telephone"),
                            resultSet.getString("email"),
                            resultSet.getInt("isAdmin"),
                            resultSet.getInt("isActived")));
                }
            }

            tasksInfo = projects.getID_tasks().split("#");
            for (String obj : tasksInfo) {
                System.out.println(obj);
            }

            for (int i = 0; i < tasksInfo.length; i++) {
                resultSet = statement.executeQuery("select * from tasks where ID ='" + tasksInfo[i] + "' " +
                        "and completed = '1' " +
                        "and completedDate >= DATE_ADD(CURRENT_DATE(), INTERVAL -7 DAY)");
                if (resultSet.next()) {
                    System.out.println(resultSet.getInt("ID") + "#" + resultSet.getString("Name") + "\n");
                    numberWeek++;
                }

            }
            for (int i = 0; i < tasksInfo.length; i++) {
                resultSet = statement.executeQuery("select * from tasks where id ='" + tasksInfo[i] + "' " +
                        "and completed = '1' " +
                        "and completedDate >= DATE_ADD(CURRENT_DATE(), INTERVAL -1 MONTH)");
                if (resultSet.next()) {
                    numberMonth++;
                }
            }
            for (int i = 0; i < tasksInfo.length; i++) {
                resultSet = statement.executeQuery("select * from tasks where id ='" + tasksInfo[i] + "' " +
                        "and completed = '1' " +
                        "and completedDate >= DATE_ADD(CURRENT_DATE(), INTERVAL -6 MONTH)");
                if (resultSet.next()) {
                    numberSixMonth++;
                }
            }
            System.out.println(numberWeek + "#" + numberMonth + "#" + numberSixMonth);


//            resultSet = statement.executeQuery("select * from tasks where id ='"++"' " +
//                    "and completed = '1'" +
//                    "and completedDate BETWEEN DATE_ADD(CURRENT_DATE(),INTERVAL -7 DAY)");
            model.addAttribute("weekCompleted", numberWeek);
            model.addAttribute("monthCompleted", numberMonth);
            model.addAttribute("sixMonthCompleted", numberSixMonth);
            model.addAttribute("managerName", manager.getName());
            model.addAttribute("managerSurname", manager.getSurname());
            model.addAttribute("managerTelephone", manager.getTelephone());
            model.addAttribute("curatorName", curators.getName());
            model.addAttribute("curatorSurname", curators.getSurname());
            model.addAttribute("curatorTelephone", curators.getTelephone());
            model.addAttribute("customerName", customers.getName());
            model.addAttribute("customerSurname", customers.getSurname());
            model.addAttribute("customerTelephone", customers.getTelephone());
            model.addAttribute("users", usersList);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "someComands";
    }

    @GetMapping("/taskToProject/{IDproj}")
    public String taskToProject(@PathVariable(value = "IDproj") String IDproj, Model model) {
        List<Tasks> tasksList = new ArrayList<>();
        idProjBuf = Integer.parseInt(IDproj);

        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from tasks");
            while (resultSet.next()) {
                tasksList.add(new Tasks(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getDate("startDate"),
                        resultSet.getDate("finishDate"),
                        resultSet.getString("IDcategories"),
                        resultSet.getInt("completed"),
                        resultSet.getDate("completedDate")));
            }
            model.addAttribute("tasks", tasksList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "taskToProject";
    }

    @PostMapping("/taskToProject")
    public String addTaskToProject(@RequestParam(value = "task", required = false) String task, Model model) {
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            List<Tasks> tasksList = new ArrayList<>();
            String[] tasksArr = task.split(",");
            ResultSet resultSet = statement.executeQuery("select * from tasks where ID ='" + idProjBuf + "'");
            while (resultSet.next()) {
                tasksList.add(new Tasks(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getDate("startDate"),
                        resultSet.getDate("finishDate"),
                        resultSet.getString("IDcategories"),
                        resultSet.getInt("completed"),
                        resultSet.getDate("completedDate")));
            }
            resultSet = statement.executeQuery("select * from projects where  ID='" + idProjBuf + "'");
            if (resultSet.next()) {
                task += "#" + resultSet.getString("ID_tasks");
            }

//            for (Tasks obj : tasksList) {
//
//                task += "," + obj.getID();
//            }
            statement.executeUpdate("update projects set ID_tasks ='" + task + "' where ID='" + idProjBuf + "'");
            return "redirect:/controlProjects";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/taskToProject";
    }

    @GetMapping("/formToStatistic")
    public String formToStatistic(Model model) {
        return "formToStatistic";
    }

    @PostMapping("/formToStatistic")
    public String formToStatistic(@RequestParam String id, Model model) {
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from projects where id= '" + id + "'");

            if (resultSet.next()) {
                bufProject = new Projects(resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("ID_tasks"));
                return "redirect:/projectStatistic";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/formToStatistic";
    }

    @GetMapping("/projectStatistic")
    public String projectStatistic(Model model) {
        try (Connection connection = DriverManager.getConnection(url, username, passDB)) {
            Statement statement = connection.createStatement();
            String[] tasksInfo = bufProject.getID_tasks().split("#");
            String completedWeek = "";
            String completedMonth = "";
            String completedSixMonth = "";
            String week = "";
            String month = "";
            String sixMonth = "";
            Calendar c;
            Calendar today = Calendar.getInstance();
            int[] tasksPerWeek = new int[7];
            int[] tasksPerMonth = new int[30];
            int[] tasksPerSixMonth = new int[180];
            ResultSet resultSet;
            bufTasksListWeek = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < tasksInfo.length; j++) {
                    resultSet = statement.executeQuery("select * from tasks where id ='" + tasksInfo[j] + "' " +
                            "and completed = '1' " +
                            "and completedDate = DATE_ADD(CURRENT_DATE() , INTERVAL -" + i + " DAY)");
                    if (resultSet.next()) {
                        tasksPerWeek[i]++;
                    }
                }
            }
            for (int i = 0; i < 30; i++) {
                for (int j = 0; j < tasksInfo.length; j++) {
                    resultSet = statement.executeQuery("select * from tasks where id ='" + tasksInfo[j] + "' " +
                            "and completed = '1' " +
                            "and completedDate = DATE_ADD(CURRENT_DATE() , INTERVAL -" + i + " DAY)");
                    if (resultSet.next()) {
                        tasksPerMonth[i]++;
                    }
                }
            }
            for (int i = 0; i < 180; i++) {
                for (int j = 0; j < tasksInfo.length; j++) {
                    resultSet = statement.executeQuery("select * from tasks where id ='" + tasksInfo[j] + "' " +
                            "and completed = '1' " +
                            "and completedDate = DATE_ADD(CURRENT_DATE() , INTERVAL -" + i + " DAY)");
                    if (resultSet.next()) {
                        tasksPerSixMonth[i]++;
                    }
                }
            }
//            System.out.println(bufTasksListWeek.toString());
//            for(int j=0;j<tasksPerWeek.length;j++)
//            System.out.println("TasksPerWeek["+j+"] = "+tasksPerWeek[j]);
//            for (int i = 0; i < tasksInfo.length; i++) {
//
//                resultSet = statement.executeQuery("select * from tasks where id ='" + tasksInfo[i] + "' " +
//                        "and completed = '1' " +
//                        "and completedDate >= DATE_ADD(CURRENT_DATE(), INTERVAL -1 MONTH)");
//                while (resultSet.next()) {
//                    bufTasksListMonth.add(new Tasks(resultSet.getInt("ID"),
//                            resultSet.getString("Name"),
//                            resultSet.getDate("startDate"),
//                            resultSet.getDate("finishDate"),
//                            resultSet.getString("IDcategories"),
//                            resultSet.getInt("completed"),
//                            resultSet.getDate("completedDate")));
//                }
//                Collections.sort(bufTasksListMonth);
//            }
//            for (int i = 0; i < tasksInfo.length; i++) {
//
//                resultSet = statement.executeQuery("select * from tasks where id ='" + tasksInfo[i] + "'" +
//                        "and completed = '1' " +
//                        "and completedDate >= DATE_ADD(CURRENT_DATE(), INTERVAL -6 MONTH)");
//                while (resultSet.next()) {
//                    bufTasksListSixMonth.add(new Tasks(resultSet.getInt("ID"),
//                            resultSet.getString("Name"),
//                            resultSet.getDate("startDate"),
//                            resultSet.getDate("finishDate"),
//                            resultSet.getString("IDcategories"),
//                            resultSet.getInt("completed"),
//                            resultSet.getDate("completedDate")));
//                }
//                Collections.sort(bufTasksListSixMonth);
//            }


            for (int i = 0; i < tasksPerWeek.length; i++) {
                completedWeek += tasksPerWeek[i] + " ";
                System.out.println("TasksPerWeek[" + i + "] = " + tasksPerWeek[i]);
            }
            for (int i = 0; i < tasksPerMonth.length; i++) {
                completedMonth += tasksPerMonth[i] + " ";
                System.out.println("TasksPerMonth[" + i + "] = " + tasksPerMonth[i]);
            }
            for (int i = 0; i < tasksPerSixMonth.length; i++) {
                completedSixMonth += tasksPerSixMonth[i] + " ";
                System.out.println("TasksPerSixMonth[" + i + "] = " + tasksPerSixMonth[i]);
            }
            c = Calendar.getInstance();
            for (int i = 0; i < 7; i++) {
                c.add(Calendar.DAY_OF_MONTH, -i);
                week += c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + " ";
                c = Calendar.getInstance();
            }
            for (int i = 0; i < 30; i++) {
                c.add(Calendar.DAY_OF_MONTH, -i);
                month += c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + " ";
                c = Calendar.getInstance();
            }
            for (int i = 0; i < 180; i++) {
                c.add(Calendar.DAY_OF_MONTH, -i);
                sixMonth += c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + " ";
                c = Calendar.getInstance();
            }
            System.out.println(week);

            model.addAttribute("week", week);
            model.addAttribute("month", month);
            model.addAttribute("sixMonth", sixMonth);
            model.addAttribute("weekCompleted", completedWeek);
            model.addAttribute("monthCompleted", completedMonth);
            model.addAttribute("sixMonthCompleted", completedSixMonth);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "projectStatistic";
    }


}