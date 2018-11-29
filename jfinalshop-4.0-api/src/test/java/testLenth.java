import com.jfinal.kit.StrKit;
import com.jfinalshop.api.common.token.TokenManager;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.MemberService;

public class testLenth {
	
	public static void main(String args[]){
		MemberService memberService = new MemberService();
		Member member = null;
		String token = "4ae8e9d29d574f339d66cb5850dcd553l4m9zs";
	       if (StrKit.notBlank(token)) {
	        	member = TokenManager.getMe().validate(token);
	        	if (member != null) {
	    			member = memberService.find(member.getId());
	    			System.out.println(member.getUsername());
	    		}
	        }
	}
}
