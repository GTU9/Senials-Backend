package com.senials.user.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserStatusChangeRequest {

    private List<Integer> checkedUsers;
    private int status;


}
