package com.zetcode.service;

import com.zetcode.model.User;
import java.util.List;

public interface IUserService {

    List<User> findAll();
}