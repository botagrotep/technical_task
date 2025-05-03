package technikal.task.fishmarket.models;

import jakarta.validation.constraints.NotEmpty;

public class RegisterRequest {
    @NotEmpty(message = "Логін не може бути пустим")
    private String login;
    @NotEmpty(message = "Пароль не може бути пустим")
    private String password;
    @NotEmpty(message = "Підтвердіть пароль")
    private String confirmPassword;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
