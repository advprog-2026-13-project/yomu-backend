package id.ac.ui.cs.advprog.yomu.backend.auth.api.dto;

import jakarta.validation.constraints.*;

public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 40)
    private String username;

    @NotBlank
    @Size(max = 100)
    private String displayName; // Tambahan

    @Email 
    private String email; // Tidak @NotBlank agar bisa daftar pakai phone saja

    private String phoneNumber; // Tambahan

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    public RegisterRequest() {}

    public String getUsername() { 
      return username; 
    }

    public void setUsername(String username) { 
      this.username = username; 
    }
    
    public String getDisplayName() { 
      return displayName; 
    }

    public void setDisplayName(String displayName) { 
      this.displayName = displayName; 
    }

    public String getEmail() { 
      return email; 
    }

    public void setEmail(String email) { 
      this.email = email; 
    }

    public String getPhoneNumber() { 
      return phoneNumber; 
    }

    public void setPhoneNumber(String phoneNumber) { 
      this.phoneNumber = phoneNumber; 
    }

    public String getPassword() { 
      return password; 
    }

    public void setPassword(String password) { 
      this.password = password; 
    }
}