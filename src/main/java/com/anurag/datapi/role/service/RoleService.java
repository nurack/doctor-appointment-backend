package com.anurag.datapi.role.service;

import com.anurag.datapi.response.Response;
import com.anurag.datapi.role.entity.Role;

import java.util.List;

public interface RoleService {

    Response<Role> createRole(Role roleRequest);

    Response<Role> updateRole(Role roleRequest);

    Response<List<Role>> getAllRoles();

    Response<?> deleteRole(Long id);
}
