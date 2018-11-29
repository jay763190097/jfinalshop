package com.jfinalshop.api.controller;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.model.Area;
import com.jfinalshop.service.AreaService;
import net.hasor.core.Inject;

import java.util.ArrayList;

@ControllerBind(controllerKey = "/api/area")
public class AreaController extends BaseAPIController {
    @Inject
    private AreaService areaService;

    public void list() {
        Long parentId = getParaToLong("parentId");
        Area parent = areaService.find(parentId);
        if (parent != null) {
            //setAttr("parent", parent);
            //setAttr("areas", new ArrayList<Area>(parent.getChildren()));
            renderJson(new ArrayList<Area>(parent.getChildren()));
        } else {
            //setAttr("areas", areaService.findRoots());
            renderJson(areaService.findRoots());
        }
    }
}
