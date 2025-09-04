package com.binod.Fitness_tracker_microservice.service;

import com.binod.Fitness_tracker_microservice.dto.RegisterDetail;
import com.binod.Fitness_tracker_microservice.dto.UserProfile;
import com.binod.Fitness_tracker_microservice.exception.ResourceNotFoundException;
import com.binod.Fitness_tracker_microservice.model.User;
import com.binod.Fitness_tracker_microservice.model.UserRole;
import com.binod.Fitness_tracker_microservice.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepo repo;
        public UserProfile getDetails(String id) {
        User user =repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        UserProfile userProfile= new UserProfile();
        userProfile.setId(user.getId());
        userProfile.setKeycloakId(user.getKeycloakId());
        userProfile.setUserRole(user.getUserRole());
        userProfile.setEmail(user.getEmail());
        userProfile.setPassword(user.getPassword());
        userProfile.setFirstName(user.getFirstName());
        userProfile.setLastName(user.getLastName());
        userProfile.setCreatedAt(user.getCreatedAt());
        userProfile.setUpdatedAt(user.getUpdatedAt());
        return userProfile;
        }

        public UserProfile createUser(RegisterDetail registerDetail){
            if(repo.existsByEmail(registerDetail.getEmail())){
                User existingUser= repo.findByEmail(registerDetail.getEmail());
                UserProfile userProfile= new UserProfile();
                userProfile.setId(existingUser.getId());
                userProfile.setUserRole(UserRole.USER);
                userProfile.setKeycloakId(existingUser.getKeycloakId());
                userProfile.setEmail(existingUser.getEmail());
                userProfile.setPassword(existingUser.getPassword());
                userProfile.setFirstName(existingUser.getFirstName());
                userProfile.setLastName(existingUser.getLastName());
                userProfile.setCreatedAt(existingUser.getCreatedAt());
                userProfile.setUpdatedAt(existingUser.getUpdatedAt());

                return userProfile;




            }
            User user = new User();
            user.setUserRole(UserRole.USER);
            user.setKeycloakId(registerDetail.getKeycloakId());
            user.setEmail(registerDetail.getEmail());
            user.setPassword(registerDetail.getPassword());
            user.setFirstName(registerDetail.getFirstName());
            user.setLastName(registerDetail.getLastName());
            repo.save(user);

            UserProfile userProfile = getUserProfile(user);

            return userProfile;

        }

    private static UserProfile getUserProfile(User user) {
        UserProfile userProfile= new UserProfile();

        userProfile.setId(user.getId());
        userProfile.setUserRole(user.getUserRole());
        userProfile.setKeycloakId(user.getKeycloakId());
        userProfile.setEmail(user.getEmail());
        userProfile.setPassword(user.getPassword());
        userProfile.setFirstName(user.getFirstName());
        userProfile.setLastName(user.getLastName());
        userProfile.setCreatedAt(user.getCreatedAt());
        userProfile.setUpdatedAt(user.getUpdatedAt());
        return userProfile;
    }

    public boolean deleteUser(String id){
            Optional<User> user = repo.findById(id);
            if (user.isPresent()) {
                repo.delete(user.get());
                return true;
            } else {
                return false;
            }
        }


    public Boolean existsByKeycloakId(String id) {
            System.out.println("The recieved id is"+id);
            return repo.existsByKeycloakId((id));
    }

    public Boolean validateByUserId(String userId) {
            return repo.existsById(userId);
    }
}

