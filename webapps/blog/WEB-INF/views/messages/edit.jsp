<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
	com.scooterframework.orm.sqldataexpress.object.RESTified,
	com.scooterframework.web.util.F,
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.R,
	com.scooterframework.web.util.W"
%>

<h2>Edit message</h2>
<%=W.errorMessage("message")%>

<%=F.formForOpen("messages", "message")%>

<p>
    <span class="required">*</span><%=F.label("b")%><br />
  <input type="text" id="message_b" name="b" value="<%=O.hv("message.b")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("c")%><br />
  <input type="text" id="message_c" name="c" value="<%=O.hv("message.c")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("d")%><br />
  <input type="text" id="message_d" name="d" value="<%=O.hv("message.d")%>" size="80" /> 
</p>

<p>
    <%=F.label("e")%><br />
  <input type="text" id="message_e" name="e" value="<%=O.hv("message.e")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("f")%><br />
  <input type="text" id="message_f" name="f" value="<%=O.hv("message.f")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("g")%><br />
  <input type="text" id="message_g" name="g" value="<%=O.hv("message.g")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("h")%><br />
  <input type="text" id="message_h" name="h" value="<%=O.hv("message.h")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("i")%><br />
  <input type="text" id="message_i" name="i" value="<%=O.hv("message.i")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("j")%><br />
  <input type="text" id="message_j" name="j" value="<%=O.hv("message.j")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("k")%><br />
  <input type="text" id="message_k" name="k" value="<%=O.hv("message.k")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("l")%><br />
  <input type="text" id="message_l" name="l" value="<%=O.hv("message.l")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("m")%><br />
  <input type="text" id="message_m" name="m" value="<%=O.hv("message.m")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("n")%><br />
  <input type="text" id="message_n" name="n" value="<%=O.hv("message.n")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("o")%><br />
  <input type="text" id="message_o" name="o" value="<%=O.hv("message.o")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("p")%><br />
  <input type="text" id="message_p" name="p" value="<%=O.hv("message.p")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("q")%><br />
  <input type="text" id="message_q" name="q" value="<%=O.hv("message.q")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("r")%><br />
  <input type="text" id="message_r" name="r" value="<%=O.hv("message.r")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("s")%><br />
  <input type="text" id="message_s" name="s" value="<%=O.hv("message.s")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("t")%><br />
  <input type="text" id="message_t" name="t" value="<%=O.hv("message.t")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("u")%><br />
  <input type="text" id="message_u" name="u" value="<%=O.hv("message.u")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("v")%><br />
  <textarea id="message_v" name="v" cols="60" rows="10"><%=O.hv("message.v")%></textarea>
</p>
<p>
    <span class="required">*</span><%=F.label("w")%><br />
  <input type="text" id="message_w" name="w" value="<%=O.hv("message.w")%>" size="30" /> (yyyy-mm-dd hh:mm:ss)
</p>

<p>
    <span class="required">*</span><%=F.label("x")%><br />
  <input type="text" id="message_x" name="x" value="<%=O.hv("message.x")%>" size="30" /> (yyyy-mm-dd hh:mm:ss)
</p>

<p>
    <span class="required">*</span><%=F.label("y")%><br />
  <input type="text" id="message_y" name="y" value="<%=O.hv("message.y")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("z")%><br />
  <input type="text" id="message_z" name="z" value="<%=O.hv("message.z")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("aa")%><br />
  <input type="text" id="message_aa" name="aa" value="<%=O.hv("message.aa")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("bb")%><br />
  <input type="text" id="message_bb" name="bb" value="<%=O.hv("message.bb")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("cc")%><br />
  <input type="text" id="message_cc" name="cc" value="<%=O.hv("message.cc")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("dd")%><br />
  <input type="text" id="message_dd" name="dd" value="<%=O.hv("message.dd")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("ee")%><br />
  <input type="text" id="message_ee" name="ee" value="<%=O.hv("message.ee")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("ff")%><br />
  <input type="text" id="message_ff" name="ff" value="<%=O.hv("message.ff")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("gg")%><br />
  <input type="text" id="message_gg" name="gg" value="<%=O.hv("message.gg")%>" size="30" /> (yyyy-mm-dd hh:mm:ss)
</p>

<p>
    <span class="required">*</span><%=F.label("hh")%><br />
  <input type="text" id="message_hh" name="hh" value="<%=O.hv("message.hh")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("ii")%><br />
  <input type="text" id="message_ii" name="ii" value="<%=O.hv("message.ii")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("jj")%><br />
  <input type="text" id="message_jj" name="jj" value="<%=O.hv("message.jj")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("kk")%><br />
  <input type="text" id="message_kk" name="kk" value="<%=O.hv("message.kk")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("ll")%><br />
  <input type="text" id="message_ll" name="ll" value="<%=O.hv("message.ll")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("mm")%><br />
  <textarea id="message_mm" name="mm" cols="60" rows="10"><%=O.hv("message.mm")%></textarea>
</p>
<p>
    <span class="required">*</span><%=F.label("nn")%><br />
  <input type="text" id="message_nn" name="nn" value="<%=O.hv("message.nn")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("oo")%><br />
  <input type="text" id="message_oo" name="oo" value="<%=O.hv("message.oo")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("pp")%><br />
  <input type="text" id="message_pp" name="pp" value="<%=O.hv("message.pp")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("qq")%><br />
  <input type="text" id="message_qq" name="qq" value="<%=O.hv("message.qq")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("rr")%><br />
  <input type="text" id="message_rr" name="rr" value="<%=O.hv("message.rr")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("ss")%><br />
  <input type="text" id="message_ss" name="ss" value="<%=O.hv("message.ss")%>" size="80" /> 
</p>

<p>
    <%=F.label("tt")%><br />
  <input type="text" id="message_tt" name="tt" value="<%=O.hv("message.tt")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("uu")%><br />
  <input type="text" id="message_uu" name="uu" value="<%=O.hv("message.uu")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("vv")%><br />
  <input type="text" id="message_vv" name="vv" value="<%=O.hv("message.vv")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("ww")%><br />
  <input type="text" id="message_ww" name="ww" value="<%=O.hv("message.ww")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("xx")%><br />
  <input type="text" id="message_xx" name="xx" value="<%=O.hv("message.xx")%>" size="30" /> (yyyy-mm-dd hh:mm:ss)
</p>

<p>
    <span class="required">*</span><%=F.label("yy")%><br />
  <input type="text" id="message_yy" name="yy" value="<%=O.hv("message.yy")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("zz")%><br />
  <input type="text" id="message_zz" name="zz" value="<%=O.hv("message.zz")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("aaa")%><br />
  <input type="text" id="message_aaa" name="aaa" value="<%=O.hv("message.aaa")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("bbb")%><br />
  <input type="text" id="message_bbb" name="bbb" value="<%=O.hv("message.bbb")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("ccc")%><br />
  <input type="text" id="message_ccc" name="ccc" value="<%=O.hv("message.ccc")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("ddd")%><br />
  <input type="text" id="message_ddd" name="ddd" value="<%=O.hv("message.ddd")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("eee")%><br />
  <input type="text" id="message_eee" name="eee" value="<%=O.hv("message.eee")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("fff")%><br />
  <input type="text" id="message_fff" name="fff" value="<%=O.hv("message.fff")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("ggg")%><br />
  <input type="text" id="message_ggg" name="ggg" value="<%=O.hv("message.ggg")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("hhh")%><br />
  <input type="text" id="message_hhh" name="hhh" value="<%=O.hv("message.hhh")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("iii")%><br />
  <input type="text" id="message_iii" name="iii" value="<%=O.hv("message.iii")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("jjj")%><br />
  <input type="text" id="message_jjj" name="jjj" value="<%=O.hv("message.jjj")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("kkk")%><br />
  <input type="text" id="message_kkk" name="kkk" value="<%=O.hv("message.kkk")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("lll")%><br />
  <input type="text" id="message_lll" name="lll" value="<%=O.hv("message.lll")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("mmm")%><br />
  <input type="text" id="message_mmm" name="mmm" value="<%=O.hv("message.mmm")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("nnn")%><br />
  <input type="text" id="message_nnn" name="nnn" value="<%=O.hv("message.nnn")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("ooo")%><br />
  <input type="text" id="message_ooo" name="ooo" value="<%=O.hv("message.ooo")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("ppp")%><br />
  <input type="text" id="message_ppp" name="ppp" value="<%=O.hv("message.ppp")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("qqq")%><br />
  <input type="text" id="message_qqq" name="qqq" value="<%=O.hv("message.qqq")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("rrr")%><br />
  <input type="text" id="message_rrr" name="rrr" value="<%=O.hv("message.rrr")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("sss")%><br />
  <input type="text" id="message_sss" name="sss" value="<%=O.hv("message.sss")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("ttt")%><br />
  <input type="text" id="message_ttt" name="ttt" value="<%=O.hv("message.ttt")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("uuu")%><br />
  <input type="text" id="message_uuu" name="uuu" value="<%=O.hv("message.uuu")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("vvv")%><br />
  <input type="text" id="message_vvv" name="vvv" value="<%=O.hv("message.vvv")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("www")%><br />
  <input type="text" id="message_www" name="www" value="<%=O.hv("message.www")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("xxx")%><br />
  <input type="text" id="message_xxx" name="xxx" value="<%=O.hv("message.xxx")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("yyy")%><br />
  <input type="text" id="message_yyy" name="yyy" value="<%=O.hv("message.yyy")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("zzz")%><br />
  <input type="text" id="message_zzz" name="zzz" value="<%=O.hv("message.zzz")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("aaaa")%><br />
  <input type="text" id="message_aaaa" name="aaaa" value="<%=O.hv("message.aaaa")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("bbbb")%><br />
  <input type="text" id="message_bbbb" name="bbbb" value="<%=O.hv("message.bbbb")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("cccc")%><br />
  <input type="text" id="message_cccc" name="cccc" value="<%=O.hv("message.cccc")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("dddd")%><br />
  <input type="text" id="message_dddd" name="dddd" value="<%=O.hv("message.dddd")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("eeee")%><br />
  <input type="text" id="message_eeee" name="eeee" value="<%=O.hv("message.eeee")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("ffff")%><br />
  <input type="text" id="message_ffff" name="ffff" value="<%=O.hv("message.ffff")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("gggg")%><br />
  <input type="text" id="message_gggg" name="gggg" value="<%=O.hv("message.gggg")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("hhhh")%><br />
  <input type="text" id="message_hhhh" name="hhhh" value="<%=O.hv("message.hhhh")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("iiii")%><br />
  <input type="text" id="message_iiii" name="iiii" value="<%=O.hv("message.iiii")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("jjjj")%><br />
  <input type="text" id="message_jjjj" name="jjjj" value="<%=O.hv("message.jjjj")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("kkkk")%><br />
  <input type="text" id="message_kkkk" name="kkkk" value="<%=O.hv("message.kkkk")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("llll")%><br />
  <input type="text" id="message_llll" name="llll" value="<%=O.hv("message.llll")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("mmmm")%><br />
  <input type="text" id="message_mmmm" name="mmmm" value="<%=O.hv("message.mmmm")%>" size="80" /> 
</p>

<p>
    <%=F.label("nnnn")%><br />
  <input type="text" id="message_nnnn" name="nnnn" value="<%=O.hv("message.nnnn")%>" size="80" /> 
</p>

<p>
    <%=F.label("oooo")%><br />
  <input type="text" id="message_oooo" name="oooo" value="<%=O.hv("message.oooo")%>" size="80" /> 
</p>

<p>
    <%=F.label("pppp")%><br />
  <input type="text" id="message_pppp" name="pppp" value="<%=O.hv("message.pppp")%>" size="80" /> 
</p>

<p>
    <%=F.label("qqqq")%><br />
  <input type="text" id="message_qqqq" name="qqqq" value="<%=O.hv("message.qqqq")%>" size="80" /> 
</p>

<p>
    <%=F.label("rrrr")%><br />
  <input type="text" id="message_rrrr" name="rrrr" value="<%=O.hv("message.rrrr")%>" size="80" /> 
</p>

<p>
    <%=F.label("ssss")%><br />
  <textarea id="message_ssss" name="ssss" cols="60" rows="10"><%=O.hv("message.ssss")%></textarea>
</p>
<p>
    <%=F.label("tttt")%><br />
  <textarea id="message_tttt" name="tttt" cols="60" rows="10"><%=O.hv("message.tttt")%></textarea>
</p>

  <input id="message_submit" name="commit" type="submit" value="Update" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
<%=F.formForClose("messages")%>

<br />

<%=W.labelLink("Show", R.resourceRecordPath("messages", (RESTified)W.request("message")))%>|
<%=W.labelLink("List", R.resourcePath("messages"))%>|
<%=W.labelLink("Paged List", R.resourcePath("messages") + "?paged=true")%>
