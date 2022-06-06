package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
	public UserService(UserRepository todoAppRepository) {
		this.userRepository = todoAppRepository;
	}

	public List<User> findAll() {

		return userRepository.findAll();
		
	}

	public User insert(User user) {
		return userRepository.save(user);
        
    }   
	
	public Integer isStored(String creator){

		Optional<User> userOptional = userRepository.findByMail(creator);
		
		if(userOptional.isEmpty()){

			//Adding the user to the table
			User userToInsert = User.builder().mail(creator).build();
			
			//Geting the id from the user
			return userRepository.save(userToInsert).getId();

		}else{

			User userProvisional =  userOptional.get();
			//Geting the id from the user
			return userProvisional.getId();
				
		}

	}

	public String findCreator(Integer id){
		
		return userRepository.findNameById(id);

	}

    
}
