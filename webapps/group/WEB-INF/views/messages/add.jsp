<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
	com.scooterframework.web.util.F,
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.R,
	com.scooterframework.web.util.W"
%>

<h2>Add message</h2>
<%=W.errorMessage("message")%>

<%=F.formForOpen("messages", "message")%>

<p>
    <span class="required">*</span><%=F.label("cid")%><br />
  <input type="text" id="message_cid" name="cid" value="<%=O.hv("message.cid")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("eid")%><br />
  <input type="text" id="message_eid" name="eid" value="<%=O.hv("message.eid")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("a")%><br />
  <input type="text" id="message_a" name="a" value="<%=O.hv("message.a")%>" size="80" /> 
</p>

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
    <span class="required">*</span><%=F.label("e")%><br />
  <input type="text" id="message_e" name="e" value="<%=O.hv("message.e")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("f")%><br />
  <input type="text" id="message_f" name="f" value="<%=O.hv("message.f")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("employeename")%><br />
  <input type="text" id="message_employeename" name="employeename" value="<%=O.hv("message.employeename")%>" size="80" /> 
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
    <%=F.label("s")%><br />
  <input type="text" id="message_s" name="s" value="<%=O.hv("message.s")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("t")%><br />
  <input type="text" id="message_t" name="t" value="<%=O.hv("message.t")%>" size="80" /> 
</p>

<p>
    <%=F.label("u")%><br />
  <input type="text" id="message_u" name="u" value="<%=O.hv("message.u")%>" size="30" /> (yyyy-mm-dd hh:mm:ss)
</p>
    <script type="text/javascript">
        jQuery(function(){jQuery('#message_u').datepicker({ dateFormat: 'yy-mm-dd' });});
    </script>
<p>
    <%=F.label("v")%><br />
  <input type="text" id="message_v" name="v" value="<%=O.hv("message.v")%>" size="30" /> (yyyy-mm-dd hh:mm:ss)
</p>
    <script type="text/javascript">
        jQuery(function(){jQuery('#message_v').datepicker({ dateFormat: 'yy-mm-dd' });});
    </script>
<p>
    <%=F.label("w")%><br />
  <input type="text" id="message_w" name="w" value="<%=O.hv("message.w")%>" size="30" /> (yyyy-mm-dd hh:mm:ss)
</p>
    <script type="text/javascript">
        jQuery(function(){jQuery('#message_w').datepicker({ dateFormat: 'yy-mm-dd' });});
    </script>
<p>
    <%=F.label("x")%><br />
  <input type="text" id="message_x" name="x" value="<%=O.hv("message.x")%>" size="30" /> (yyyy-mm-dd hh:mm:ss)
</p>
    <script type="text/javascript">
        jQuery(function(){jQuery('#message_x').datepicker({ dateFormat: 'yy-mm-dd' });});
    </script>
<p>
    <span class="required">*</span><%=F.label("y")%><br />
  <textarea id="message_y" name="y" cols="60" rows="10"><%=O.hv("message.y")%></textarea>
</p>

<p>
    <span class="required">*</span><%=F.label("z")%><br />
  <textarea id="message_z" name="z" cols="60" rows="10"><%=O.hv("message.z")%></textarea>
</p>

<p>
    <%=F.label("aa")%><br />
  <textarea id="message_aa" name="aa" cols="60" rows="10"><%=O.hv("message.aa")%></textarea>
</p>

<p>
    <%=F.label("bb")%><br />
  <textarea id="message_bb" name="bb" cols="60" rows="10"><%=O.hv("message.bb")%></textarea>
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
  <input type="text" id="message_gg" name="gg" value="<%=O.hv("message.gg")%>" size="80" /> 
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
    <%=F.label("kk")%><br />
  <input type="text" id="message_kk" name="kk" value="<%=O.hv("message.kk")%>" size="30" /> (yyyy-mm-dd hh:mm:ss)
</p>
    <script type="text/javascript">
        jQuery(function(){jQuery('#message_kk').datepicker({ dateFormat: 'yy-mm-dd' });});
    </script>
<p>
    <span class="required">*</span><%=F.label("ll")%><br />
  <input type="text" id="message_ll" name="ll" value="<%=O.hv("message.ll")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("mm")%><br />
  <input type="text" id="message_mm" name="mm" value="<%=O.hv("message.mm")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("nn")%><br />
  <input type="text" id="message_nn" name="nn" value="<%=O.hv("message.nn")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("oo")%><br />
  <textarea id="message_oo" name="oo" cols="60" rows="10"><%=O.hv("message.oo")%></textarea>
</p>

<p>
    <span class="required">*</span><%=F.label("pp")%><br />
  <textarea id="message_pp" name="pp" cols="60" rows="10"><%=O.hv("message.pp")%></textarea>
</p>

<p>
    <span class="required">*</span><%=F.label("qq")%><br />
  <textarea id="message_qq" name="qq" cols="60" rows="10"><%=O.hv("message.qq")%></textarea>
</p>

<p>
    <span class="required">*</span><%=F.label("rr")%><br />
  <textarea id="message_rr" name="rr" cols="60" rows="10"><%=O.hv("message.rr")%></textarea>
</p>

<p>
    <span class="required">*</span><%=F.label("ss")%><br />
  <textarea id="message_ss" name="ss" cols="60" rows="10"><%=O.hv("message.ss")%></textarea>
</p>

<p>
    <span class="required">*</span><%=F.label("tt")%><br />
  <textarea id="message_tt" name="tt" cols="60" rows="10"><%=O.hv("message.tt")%></textarea>
</p>

<p>
    <span class="required">*</span><%=F.label("uu")%><br />
  <textarea id="message_uu" name="uu" cols="60" rows="10"><%=O.hv("message.uu")%></textarea>
</p>

<p>
    <span class="required">*</span><%=F.label("vv")%><br />
  <textarea id="message_vv" name="vv" cols="60" rows="10"><%=O.hv("message.vv")%></textarea>
</p>

<p>
    <span class="required">*</span><%=F.label("ww")%><br />
  <textarea id="message_ww" name="ww" cols="60" rows="10"><%=O.hv("message.ww")%></textarea>
</p>

<p>
    <span class="required">*</span><%=F.label("xx")%><br />
  <textarea id="message_xx" name="xx" cols="60" rows="10"><%=O.hv("message.xx")%></textarea>
</p>

<p>
    <span class="required">*</span><%=F.label("yy")%><br />
  <textarea id="message_yy" name="yy" cols="60" rows="10"><%=O.hv("message.yy")%></textarea>
</p>

<p>
    <span class="required">*</span><%=F.label("zz")%><br />
  <textarea id="message_zz" name="zz" cols="60" rows="10"><%=O.hv("message.zz")%></textarea>
</p>

<p>
    <span class="required">*</span><%=F.label("a1")%><br />
  <textarea id="message_a1" name="a1" cols="60" rows="10"><%=O.hv("message.a1")%></textarea>
</p>

<p>
    <span class="required">*</span><%=F.label("a2")%><br />
  <textarea id="message_a2" name="a2" cols="60" rows="10"><%=O.hv("message.a2")%></textarea>
</p>

<p>
    <span class="required">*</span><%=F.label("a3")%><br />
  <textarea id="message_a3" name="a3" cols="60" rows="10"><%=O.hv("message.a3")%></textarea>
</p>

<p>
    <span class="required">*</span><%=F.label("a4")%><br />
  <textarea id="message_a4" name="a4" cols="60" rows="10"><%=O.hv("message.a4")%></textarea>
</p>


  <input id="message_submit" name="commit" type="submit" value="Create" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
<%=F.formForClose("messages")%>

<br />

<%=W.labelLink("List", R.resourcePath("messages"))%>|
<%=W.labelLink("Paged List", R.resourcePath("messages") + "?paged=true")%>
