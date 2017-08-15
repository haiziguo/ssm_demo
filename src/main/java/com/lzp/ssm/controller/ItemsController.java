package com.lzp.ssm.controller;

/*

*/

import com.lzp.ssm.controller.converter.validation.ValidGroup1;
import com.lzp.ssm.exception.CustomException;
import com.lzp.ssm.po.ItemsCustom;
import com.lzp.ssm.po.ItemsQueryVo;
import com.lzp.ssm.service.ItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lenovo on 2017/8/11.
 *//*

public class ItemsController implements HttpRequestHandler {
    public void handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        //调用service查找数据库，查询商品列表，这里使用静态数据模拟
        List<Items> itemsList = new ArrayList<Items>();

        //向list中填充静态数据
        Items items_1 = new Items();
        items_1.setName("联想笔记本");
        items_1.setPrice(6000f);
        items_1.setDetail("ThinkPad T430 联想笔记本电脑！");

        Items items_2 = new Items();
        items_2.setName("苹果手机");
        items_2.setPrice(5000f);
        items_2.setDetail("iphone6苹果手机！");

        itemsList.add(items_1);
        itemsList.add(items_2);

        //设置模型数据
        httpServletRequest.setAttribute("itemsList",itemsList);

        //设置转发的视图
        httpServletRequest.getRequestDispatcher("/WEB-INF/pages/items/itemsList.jsp").forward(httpServletRequest,httpServletResponse);

    }
}*/

//使用@Controller来标识它是一个控制器
@Controller
@RequestMapping("/items")
//为了对url进行分类管理 ，可以在这里定义根路径，最终访问url是根路径+子路径
//比如：商品列表：/items/queryItems.action
public class ItemsController {

    @Autowired
    private ItemsService itemsService;


    @ModelAttribute("itemtypes")
    public Map<String,String> getItemTypes(){
        Map<String,String> itemtypes=new HashMap<String,String>();
        itemtypes.put("101", "数码");
        itemtypes.put("102", "母婴");
        return itemtypes;
    }


    //商品查询列表
    @RequestMapping(value = "/queryItems")
    //实现 对queryItems方法和url进行映射，一个方法对应一个url
    //一般建议将url和方法写成一样
    public ModelAndView queryItems(Model model,HttpServletRequest request, ItemsQueryVo itemsQueryVo) throws Exception{
        //调用service查找数据库，查询商品列表
        List<ItemsCustom> itemsList = itemsService.findItemsList(itemsQueryVo);

        //返回ModelAndView
        ModelAndView modelAndView = new ModelAndView();
        //相当于request的setAttribute方法,在jsp页面中通过itemsList取数据
        modelAndView.addObject("itemsList",itemsList);
        model.addAttribute("items", itemsQueryVo);
        //指定视图
        //下边的路径，如果在视图解析器中配置jsp的路径前缀和后缀，修改为items/itemsList
        //modelAndView.setViewName("/WEB-INF/jsp/items/itemsList.jsp");
        //下边的路径配置就可以不在程序中指定jsp路径的前缀和后缀
        modelAndView.setViewName("items/itemsList");

        return modelAndView;
    }
    //商品信息修改页面显示
    @RequestMapping("/editItems")
    //限制http请求方法，可以post和get
    //@RequestMapping(value="/editItems",method={RequestMethod.POST, RequestMethod.GET})
    public String editItems(Model model ,@RequestParam(value="id",required = true)Integer items_id)throws Exception {

        //调用service根据商品id查询商品信息
        ItemsCustom itemsCustom = itemsService.findItemsById(items_id);

        //判断商品是否为空，根据id没有查询到商品，抛出异常，提示用户商品信息不存在
        if(itemsCustom == null){
            throw new CustomException("修改的商品信息不存在!");
        }
        //将商品信息放到model
        model.addAttribute("items", itemsCustom);

        //商品修改页面
//        modelAndView.setViewName("items/editItems");

        return "items/editItems";
    }

    //商品信息修改提交
    @RequestMapping("/editItemsSubmit")
    public String editItemsSubmit(
            Model model,
            HttpServletRequest request,
            Integer id,
            @ModelAttribute("items")
            @Validated(value = ValidGroup1.class)ItemsCustom itemsCustom,
            BindingResult bindingResult,
            MultipartFile items_pic)throws Exception {

        //获取校验错误信息
        if(bindingResult.hasErrors()){
            // 输出错误信息
            List<ObjectError> allErrors = bindingResult.getAllErrors();

            for (ObjectError objectError :allErrors){
                // 输出错误信息
                System.out.println(objectError.getDefaultMessage());
            }
            // 将错误信息传到页面
            model.addAttribute("allErrors", allErrors);

            //可以直接使用model将提交pojo回显到页面
            model.addAttribute("items", itemsCustom);

            // 出错重新到商品修改页面
            return "items/editItems";
        }
        //原始名称
        String originalFilename = items_pic.getOriginalFilename();
//上传图片
        if(items_pic!=null && originalFilename!=null && originalFilename.length()>0){

            //存储图片的物理路径
            String pic_path = "D:\\tmp\\";


            //新的图片名称
            String newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
            //新图片
            File newFile = new File(pic_path+newFileName);

            //将内存中的数据写入磁盘
            items_pic.transferTo(newFile);

            //将新图片名称写到itemsCustom中
            itemsCustom.setPic(newFileName);

        }
        itemsService.updateItems(id,itemsCustom);
        return "forward:queryItems.action";
    }

    @RequestMapping("/deleteItems")
    public String deleteItems(Integer[]  items_id) throws  Exception{
        System.out.println(items_id);
        return  "success";
    }
    @RequestMapping("/json")
    public  String json()
    {
        return  "jsonTest";
    }


    //查询商品信息，输出json
    //itemsView/{id}里边的{id}表示占位符，通过@PathVariable获取占位符中的参数，
    //@PathVariable中名称要和占位符一致，形参名无需和其一致
    //如果占位符中的名称和形参名一致，在@PathVariable可以不指定名称
    @RequestMapping("/itemsView/{id}")
    public
    @ResponseBody
    ItemsCustom itemsView(@PathVariable("id") Integer items_id) throws Exception {

        //调用service查询商品信息
        ItemsCustom itemsCustom = itemsService.findItemsById(items_id);

        return itemsCustom;

    }
}