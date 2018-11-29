import com.jfinalshop.shiro.session.RedisManager;

/**
 * java类简单作用描述
 *
 * @ProjectName: jfinalshop-4.0$
 * @Package: PACKAGE_NAME$
 * @ClassName: $TYPE_NAME$
 * @Description: java类作用描述
 * @Author: 作者姓名
 * @CreateDate: 2018/4/17$ 9:54$
 * @UpdateUser: 作者姓名
 * @UpdateDate: 2018/4/17$ 9:54$
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>Copyright: Copyright (c) 2018$</p>
 */
public class test {

    public static void setRs(RedisManager rs) {
            rs.set("name","焦荣国");
            String result = rs.get("name");
            System.out.println(result);
        }

    public static void main(String[] args) {
        RedisManager rs = new RedisManager();
        setRs(rs);

    }
    }

