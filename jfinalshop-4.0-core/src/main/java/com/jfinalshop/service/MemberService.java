package com.jfinalshop.service;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.Principal;
import com.jfinalshop.RequestContextHolder;
import com.jfinalshop.Setting;
import com.jfinalshop.dao.DepositLogDao;
import com.jfinalshop.dao.MemberDao;
import com.jfinalshop.dao.MemberRankDao;
import com.jfinalshop.dao.PointLogDao;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.DepositLog;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.model.PointLog;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.SystemUtils;

/**
 * Service - 会员
 * 
 * 
 */
@Singleton
public class MemberService extends BaseService<Member> {

	/**
	 * 构造方法
	 */
	public MemberService() {
		super(Member.class);
	}
	
	@Inject
	private MemberDao memberDao;
	@Inject
	private MemberRankDao memberRankDao;
	@Inject
	private DepositLogDao depositLogDao;
	@Inject
	private PointLogDao pointLogDao;
	@Inject
	private MailService mailService;
	@Inject
	private SmsService smsService;
	
	/**
	 * 判断用户名是否存在
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 用户名是否存在
	 */
	public boolean usernameExists(String username) {
		return memberDao.usernameExists(username);
	}

	/**
	 * 判断用户名是否禁用
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 用户名是否禁用
	 */
	public boolean usernameDisabled(String username) {
		Assert.hasText(username);

		Setting setting = SystemUtils.getSetting();
		if (setting.getDisabledUsernames() != null) {
			for (String disabledUsername : setting.getDisabledUsernames()) {
				if (StringUtils.containsIgnoreCase(username, disabledUsername)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断E-mail是否存在
	 * 
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return E-mail是否存在
	 */
	public boolean emailExists(String email) {
		return memberDao.emailExists(email);
	}

	/**
	 * 判断E-mail是否唯一
	 * 
	 * @param previousEmail
	 *            修改前E-mail(忽略大小写)
	 * @param currentEmail
	 *            当前E-mail(忽略大小写)
	 * @return E-mail是否唯一
	 */
	public boolean emailUnique(String previousEmail, String currentEmail) {
		if (StringUtils.equalsIgnoreCase(previousEmail, currentEmail)) {
			return true;
		}
		return !memberDao.emailExists(currentEmail);
	}
	/**
	 * 判断手机号码是否被注册
	 * 
	 * @param phone
	 *            
	 * @return phone是否存在
	 */
	public boolean phoneExists(String phone) {
		return memberDao.phoneExists(phone);
	}

	/**
	 * 查找会员
	 * 
	 * @param loginPluginId
	 *            登录插件ID
	 * @param openId
	 *            openID
	 * @return 会员，若不存在则返回null
	 */
	public Member find(String loginPluginId, String openId) {
		return memberDao.find(loginPluginId, openId);
	}

	/**
	 * 根据用户名查找会员
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 会员，若不存在则返回null
	 */
	public Member findByUsername(String username) {
		return memberDao.findByUsername(username);
	}
	
	/**
	 * 根据openId查找会员
	 * 
	 * @param open_id
	 *            
	 * @return 会员，若不存在则返回null
	 */
	public Member findByOpenId(String openId) {
		return memberDao.findByOpenId(openId);
	}

	/**
	 * 根据E-mail查找会员
	 * 
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return 会员，若不存在则返回null
	 */
	public List<Member> findListByEmail(String email) {
		return memberDao.findListByEmail(email);
	}

	/**
	 * 查找会员分页
	 * 
	 * @param rankingType
	 *            排名类型
	 * @param pageable
	 *            分页信息
	 * @return 会员分页
	 */
	public Page<Member> findPage(Member.RankingType rankingType, Pageable pageable) {
		return memberDao.findPage(rankingType, pageable);
	}

	/**
	 * 判断会员是否登录
	 * 
	 * @return 会员是否登录
	 */
	public boolean isAuthenticated() {
		HttpServletRequest requestAttributes = RequestContextHolder.currentRequestAttributes();
		return requestAttributes != null && requestAttributes.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME) != null;
	}

	/**
	 * 获取当前登录会员
	 * 
	 * @return 当前登录会员，若不存在则返回null
	 */
	public Member getCurrent() {
		return getCurrent(false);
	}

	/**
	 * 获取当前登录会员
	 * 
	 * @param lock
	 *            是否锁定
	 * @return 当前登录会员，若不存在则返回null
	 */
	public Member getCurrent(boolean lock) {
		HttpServletRequest request = RequestContextHolder.currentRequestAttributes();
		Principal principal = request != null ? (Principal) request.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME) : null;
		Long id = principal != null ? principal.getId() : null;
		if (id != null) {
			return memberDao.find(id);
		}
		return null;
	}

	/**
	 * 获取当前登录用户名
	 * 
	 * @return 当前登录用户名，若不存在则返回null
	 */
	public String getCurrentUsername() {
		HttpServletRequest requestAttributes = RequestContextHolder.currentRequestAttributes();
		Principal principal = requestAttributes != null ? (Principal) requestAttributes.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME) : null;
		return principal != null ? principal.getUsername() : null;
	}

	/**
	 * 增加余额
	 * 
	 * @param member
	 *            会员
	 * @param amount
	 *            值
	 * @param type
	 *            类型
	 * @param operator
	 *            操作员
	 * @param memo
	 *            备注
	 */
	public void addBalance(Member member, BigDecimal amount, DepositLog.Type type, Admin operator, String memo) {
		Assert.notNull(member);
		Assert.notNull(amount);
		Assert.notNull(type);

		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		Assert.notNull(member.getBalance());
		Assert.state(member.getBalance().add(amount).compareTo(BigDecimal.ZERO) >= 0);

		member.setBalance(member.getBalance().add(amount));
		memberDao.update(member);

		DepositLog depositLog = new DepositLog();
		depositLog.setType(type.ordinal());
		depositLog.setCredit(amount.compareTo(BigDecimal.ZERO) > 0 ? amount : BigDecimal.ZERO);
		depositLog.setDebit(amount.compareTo(BigDecimal.ZERO) < 0 ? amount.abs() : BigDecimal.ZERO);
		depositLog.setBalance(member.getBalance());
		depositLog.setOperator(operator);
		depositLog.setMemo(memo);
		depositLog.setMemberId(member.getId());
		depositLogDao.save(depositLog);
	}

	/**
	 * 增加积分
	 * 
	 * @param member
	 *            会员
	 * @param amount
	 *            值
	 * @param type
	 *            类型
	 * @param operator
	 *            操作员
	 * @param memo
	 *            备注
	 */
	public void addPoint(Member member, long amount, PointLog.Type type, Admin operator, String memo) {
		Assert.notNull(member);
		Assert.notNull(type);

		if (amount == 0) {
			return;
		}

		Assert.notNull(member.getPoint());
		Assert.state(member.getPoint() + amount >= 0);

		member.setPoint(member.getPoint() + amount);
		memberDao.update(member);

		PointLog pointLog = new PointLog();
		pointLog.setType(type.ordinal());
		pointLog.setCredit(amount > 0 ? amount : 0L);
		pointLog.setDebit(amount < 0 ? Math.abs(amount) : 0L);
		pointLog.setBalance(member.getPoint());
		pointLog.setOperator(operator);
		pointLog.setMemo(memo);
		pointLog.setMemberId(member.getId());
		pointLogDao.save(pointLog);
	}

	/**
	 * 增加消费金额
	 * 
	 * @param member
	 *            会员
	 * @param amount
	 *            值
	 */
	public void addAmount(Member member, BigDecimal amount) {
		Assert.notNull(member);
		Assert.notNull(amount);

		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		Assert.notNull(member.getAmount());
		Assert.state(member.getAmount().add(amount).compareTo(BigDecimal.ZERO) >= 0);

		member.setAmount(member.getAmount().add(amount));
		MemberRank memberRank = member.getMemberRank();
		if (memberRank != null && BooleanUtils.isFalse(memberRank.getIsSpecial())) {
			MemberRank newMemberRank = memberRankDao.findByAmount(member.getAmount());
			if (newMemberRank != null && newMemberRank.getAmount() != null && newMemberRank.getAmount().compareTo(memberRank.getAmount()) > 0) {
				member.setMemberRank(newMemberRank);
			}
		}
		memberDao.update(member);
	}

	public Member save(Member member) {
		Assert.notNull(member);

		Member pMember = super.save(member);
		mailService.sendRegisterMemberMail(pMember);
		smsService.sendRegisterMemberSms(pMember);
		return pMember;
	}

}