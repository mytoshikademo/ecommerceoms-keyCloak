package com.mytoshika.ecommerceoms.service.interfaces;

import com.mytoshika.ecommerceoms.entity.User;

public interface EmailService {

    void sendWelcomeEmail(User user);
}
