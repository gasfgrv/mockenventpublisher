package com.gasfgrv.mockenventpublisher.mock;

import com.exemplo.User;
import org.instancio.Instancio;
import org.instancio.Select;

public class UserMock {

    public static User generate() {
        return Instancio.of(User.class)
                .generate(Select.field(User::getId), gen -> gen.text().uuid())
                .generate(Select.field(User::getName), gen -> gen.text().word().noun())
                .generate(Select.field(User::getAge), gen -> gen.ints().range(10, 75))
                .create();
    }

}
