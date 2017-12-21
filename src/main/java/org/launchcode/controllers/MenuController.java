package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("menu")
public class MenuController {
    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private MenuDao menuDao;

    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "My Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        model.addAttribute("categories", menuDao.findAll());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(@ModelAttribute @Valid Menu newMenu,
                                     Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);
        return "redirect:view/" + newMenu.getId();
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int menuId){
        Menu viewMenu = menuDao.findOne(menuId);
        model.addAttribute("menu", viewMenu);
        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int menuId){
        Menu viewMenu = menuDao.findOne(menuId);
        AddMenuItemForm newItem = new AddMenuItemForm(viewMenu, cheeseDao.findAll());

        model.addAttribute("form", newItem);
        model.addAttribute("title", "Add item to menu: " + viewMenu.getName());
        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String processAddItem(@ModelAttribute @Valid AddMenuItemForm newItem,
                                 Errors errors, Model model) {
        if (errors.hasErrors()) {
            return "menu/add-item";
        }

        Menu theMenu = menuDao.findOne(newItem.getMenuId());
        Cheese newCheese = cheeseDao.findOne(newItem.getCheeseId());

        theMenu.addItem(newCheese);

        menuDao.save(theMenu);
        return "redirect:view/" + theMenu.getId();
    }

    @RequestMapping(value = "remove-item/{menuId}", method = RequestMethod.GET)
    public String removeItem(Model model, @PathVariable int menuId){
        Menu viewMenu = menuDao.findOne(menuId);
        AddMenuItemForm newItem = new AddMenuItemForm(viewMenu, cheeseDao.findAll());

        model.addAttribute("form", newItem);
        model.addAttribute("title", "Remove item from menu: " + viewMenu.getName());
        return "menu/remove-item";
    }

    @RequestMapping(value = "remove-item", method = RequestMethod.POST)
    public String processRemoveItem(@ModelAttribute @Valid AddMenuItemForm newItem,
                                    Errors errors, Model model) {
        if (errors.hasErrors()) {
            return "menu/remove-item";
        }

        Menu theMenu = menuDao.findOne(newItem.getMenuId());
        Cheese newCheese = cheeseDao.findOne(newItem.getCheeseId());

        theMenu.removeItem(newCheese);

        menuDao.save(theMenu);
        return "redirect:view/" + theMenu.getId();
    }
}