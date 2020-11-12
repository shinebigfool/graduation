package com.example.graduate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.graduate.codeEnum.RetCodeEnum;
import com.example.graduate.dto.DTO;
import com.example.graduate.dto.ListDTO;
import com.example.graduate.pojo.AdminMenu;
import com.example.graduate.pojo.AdminRole;
import com.example.graduate.pojo.AdminRoleMenu;
import com.example.graduate.service.AdminMenuService;
import com.example.graduate.service.AdminRoleMenuService;
import com.example.graduate.service.AdminRoleService;
import com.example.graduate.service.RoleServiceGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoleServiceGatewayImpl implements RoleServiceGateway {
    @Autowired
    private AdminRoleService adminRoleService;
    @Autowired
    private AdminRoleMenuService adminRoleMenuService;
    @Autowired
    private AdminMenuService adminMenuService;
    @Override
    public ListDTO listRoles() {
        List<AdminRole> roles = adminRoleService.list();
        List<AdminMenu> menus;
        for (AdminRole role : roles) {
            menus = getMenusByRoleId(role.getId());
            role.setMenus(menus);
        }
        ListDTO listDTO = new ListDTO(RetCodeEnum.SUCCEED);
        listDTO.setRetList(roles);
        return listDTO;
    }

    @Override
    public List<AdminMenu> getMenusByRoleId(int id) {
        LambdaQueryWrapper<AdminRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRoleMenu::getRid,id);
        List<AdminRoleMenu> list = adminRoleMenuService.list(wrapper);
        List<Integer> mids = list.stream().map(AdminRoleMenu::getMid).collect(Collectors.toList());
        if(mids.size()==0){
            return null;
        }
        List<AdminMenu> menus = adminMenuService.listByIds(mids);
        handleMenus(menus);
        return menus;
    }
    @Transactional
    @Override
    public DTO updateRoleMenu(int rid, Map<String, List<Integer>> menusIds) throws Exception{
        List<Integer> mids = menusIds.get("menusIds");
        LambdaQueryWrapper<AdminRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRoleMenu::getRid,rid);
        List<Integer> ids = adminRoleMenuService.list(wrapper).stream().map(AdminRoleMenu::getId).collect(Collectors.toList());
        if(ids.size()>0){
            adminRoleMenuService.removeByIds(ids);
        }

        List<AdminRoleMenu> newRelations = new ArrayList<>();
        for (Integer mid : mids) {
            AdminRoleMenu adminRoleMenu = new AdminRoleMenu();
            adminRoleMenu.setMid(mid);
            adminRoleMenu.setRid(rid);
            newRelations.add(adminRoleMenu);
        }
        adminRoleMenuService.saveBatch(newRelations);
        return new DTO(RetCodeEnum.SUCCEED);
    }

    /**
     * 处理层级菜单，将子菜单加在父菜单的children下，并从menu列表移出
     *
     * @param menus 目标拥有的所有菜单
     * @return void
     */
    public void handleMenus(List<AdminMenu> menus) {
        menus.forEach(m -> {
            List<AdminMenu> children = getAllByParentId(m.getId());
            m.setChildren(children);
        });

        menus.removeIf(m -> m.getParentId() != 0);
    }
    /**
     *
     *
     * @param parentId 查找在该id下的子菜单
     * @return java.util.List<com.example.graduate.pojo.AdminMenu>
     */
    private List<AdminMenu> getAllByParentId(int parentId){
        LambdaQueryWrapper<AdminMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminMenu::getParentId,parentId);
        return adminMenuService.list(wrapper);
    }
}
