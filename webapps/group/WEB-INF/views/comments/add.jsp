<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="
	com.scooterframework.web.util.F,
	com.scooterframework.web.util.O,
	com.scooterframework.web.util.R,
	com.scooterframework.web.util.W"
%>

<h2>Add comment</h2>
<%=W.errorMessage("comment")%>

<%=F.formForOpen("comments", "comment")%>

<p>
    <span class="required">*</span><%=F.label("cid")%><br />
  <input type="text" id="comment_cid" name="cid" value="<%=O.hv("comment.cid")%>" size="80" /> 
</p>

<p>
    <span class="required">*</span><%=F.label("eid")%><br />
  <input type="text" id="comment_eid" name="eid" value="<%=O.hv("comment.eid")%>" size="80" /> 
</p>

<p>
    <%=F.label("a")%><br />
  <input type="text" id="comment_a" name="a" value="<%=O.hv("comment.a")%>" size="80" /> 
</p>

<p>
    <%=F.label("b")%><br />
  <input type="text" id="comment_b" name="b" value="<%=O.hv("comment.b")%>" size="80" /> 
</p>

<p>
    <%=F.label("c")%><br />
  <input type="text" id="comment_c" name="c" value="<%=O.hv("comment.c")%>" size="80" /> 
</p>

<p>
    <%=F.label("d")%><br />
  <input type="text" id="comment_d" name="d" value="<%=O.hv("comment.d")%>" size="80" /> 
</p>

<p>
    <%=F.label("e")%><br />
  <input type="text" id="comment_e" name="e" value="<%=O.hv("comment.e")%>" size="80" /> 
</p>

<p>
    <%=F.label("f")%><br />
  <input type="text" id="comment_f" name="f" value="<%=O.hv("comment.f")%>" size="80" /> 
</p>

<p>
    <%=F.label("employeename")%><br />
  <input type="text" id="comment_employeename" name="employeename" value="<%=O.hv("comment.employeename")%>" size="80" /> 
</p>

<p>
    <%=F.label("g")%><br />
  <input type="text" id="comment_g" name="g" value="<%=O.hv("comment.g")%>" size="80" /> 
</p>

<p>
    <%=F.label("h")%><br />
  <input type="text" id="comment_h" name="h" value="<%=O.hv("comment.h")%>" size="80" /> 
</p>

<p>
    <%=F.label("i")%><br />
  <input type="text" id="comment_i" name="i" value="<%=O.hv("comment.i")%>" size="80" /> 
</p>

<p>
    <%=F.label("j")%><br />
  <input type="text" id="comment_j" name="j" value="<%=O.hv("comment.j")%>" size="80" /> 
</p>

<p>
    <%=F.label("k")%><br />
  <input type="text" id="comment_k" name="k" value="<%=O.hv("comment.k")%>" size="80" /> 
</p>

<p>
    <%=F.label("l")%><br />
  <input type="text" id="comment_l" name="l" value="<%=O.hv("comment.l")%>" size="80" /> 
</p>

<p>
    <%=F.label("m")%><br />
  <input type="text" id="comment_m" name="m" value="<%=O.hv("comment.m")%>" size="80" /> 
</p>

<p>
    <%=F.label("n")%><br />
  <input type="text" id="comment_n" name="n" value="<%=O.hv("comment.n")%>" size="80" /> 
</p>

<p>
    <%=F.label("o")%><br />
  <input type="text" id="comment_o" name="o" value="<%=O.hv("comment.o")%>" size="80" /> 
</p>

<p>
    <%=F.label("p")%><br />
  <input type="text" id="comment_p" name="p" value="<%=O.hv("comment.p")%>" size="80" /> 
</p>

<p>
    <%=F.label("q")%><br />
  <input type="text" id="comment_q" name="q" value="<%=O.hv("comment.q")%>" size="80" /> 
</p>

<p>
    <%=F.label("r")%><br />
  <input type="text" id="comment_r" name="r" value="<%=O.hv("comment.r")%>" size="80" /> 
</p>

<p>
    <%=F.label("s")%><br />
  <input type="text" id="comment_s" name="s" value="<%=O.hv("comment.s")%>" size="80" /> 
</p>

<p>
    <%=F.label("t")%><br />
  <input type="text" id="comment_t" name="t" value="<%=O.hv("comment.t")%>" size="80" /> 
</p>

<p>
    <%=F.label("u")%><br />
  <input type="text" id="comment_u" name="u" value="<%=O.hv("comment.u")%>" size="30" /> (yyyy-mm-dd hh:mm:ss)
</p>
    <script type="text/javascript">
        jQuery(function(){jQuery('#comment_u').datepicker({ dateFormat: 'yy-mm-dd' });});
    </script>
<p>
    <%=F.label("v")%><br />
  <input type="text" id="comment_v" name="v" value="<%=O.hv("comment.v")%>" size="30" /> (yyyy-mm-dd hh:mm:ss)
</p>
    <script type="text/javascript">
        jQuery(function(){jQuery('#comment_v').datepicker({ dateFormat: 'yy-mm-dd' });});
    </script>
<p>
    <%=F.label("w")%><br />
  <input type="text" id="comment_w" name="w" value="<%=O.hv("comment.w")%>" size="30" /> (yyyy-mm-dd hh:mm:ss)
</p>
    <script type="text/javascript">
        jQuery(function(){jQuery('#comment_w').datepicker({ dateFormat: 'yy-mm-dd' });});
    </script>
<p>
    <%=F.label("x")%><br />
  <input type="text" id="comment_x" name="x" value="<%=O.hv("comment.x")%>" size="30" /> (yyyy-mm-dd hh:mm:ss)
</p>
    <script type="text/javascript">
        jQuery(function(){jQuery('#comment_x').datepicker({ dateFormat: 'yy-mm-dd' });});
    </script>
<p>
    <%=F.label("y")%><br />
  <textarea id="comment_y" name="y" cols="60" rows="10"><%=O.hv("comment.y")%></textarea>
</p>

<p>
    <%=F.label("z")%><br />
  <textarea id="comment_z" name="z" cols="60" rows="10"><%=O.hv("comment.z")%></textarea>
</p>

<p>
    <%=F.label("aa")%><br />
  <textarea id="comment_aa" name="aa" cols="60" rows="10"><%=O.hv("comment.aa")%></textarea>
</p>

<p>
    <%=F.label("bb")%><br />
  <textarea id="comment_bb" name="bb" cols="60" rows="10"><%=O.hv("comment.bb")%></textarea>
</p>

<p>
    <%=F.label("cc")%><br />
  <input type="text" id="comment_cc" name="cc" value="<%=O.hv("comment.cc")%>" size="80" /> 
</p>

<p>
    <%=F.label("dd")%><br />
  <input type="text" id="comment_dd" name="dd" value="<%=O.hv("comment.dd")%>" size="80" /> 
</p>

<p>
    <%=F.label("ee")%><br />
  <input type="text" id="comment_ee" name="ee" value="<%=O.hv("comment.ee")%>" size="80" /> 
</p>

<p>
    <%=F.label("ff")%><br />
  <input type="text" id="comment_ff" name="ff" value="<%=O.hv("comment.ff")%>" size="80" /> 
</p>

<p>
    <%=F.label("gg")%><br />
  <input type="text" id="comment_gg" name="gg" value="<%=O.hv("comment.gg")%>" size="80" /> 
</p>

<p>
    <%=F.label("hh")%><br />
  <input type="text" id="comment_hh" name="hh" value="<%=O.hv("comment.hh")%>" size="80" /> 
</p>

<p>
    <%=F.label("ii")%><br />
  <input type="text" id="comment_ii" name="ii" value="<%=O.hv("comment.ii")%>" size="80" /> 
</p>

<p>
    <%=F.label("jj")%><br />
  <input type="text" id="comment_jj" name="jj" value="<%=O.hv("comment.jj")%>" size="80" /> 
</p>

<p>
    <%=F.label("kk")%><br />
  <input type="text" id="comment_kk" name="kk" value="<%=O.hv("comment.kk")%>" size="30" /> (yyyy-mm-dd hh:mm:ss)
</p>
    <script type="text/javascript">
        jQuery(function(){jQuery('#comment_kk').datepicker({ dateFormat: 'yy-mm-dd' });});
    </script>
<p>
    <%=F.label("ll")%><br />
  <input type="text" id="comment_ll" name="ll" value="<%=O.hv("comment.ll")%>" size="80" /> 
</p>

<p>
    <%=F.label("mm")%><br />
  <input type="text" id="comment_mm" name="mm" value="<%=O.hv("comment.mm")%>" size="80" /> 
</p>

<p>
    <%=F.label("nn")%><br />
  <input type="text" id="comment_nn" name="nn" value="<%=O.hv("comment.nn")%>" size="80" /> 
</p>

<p>
    <%=F.label("oo")%><br />
  <textarea id="comment_oo" name="oo" cols="60" rows="10"><%=O.hv("comment.oo")%></textarea>
</p>

<p>
    <%=F.label("pp")%><br />
  <textarea id="comment_pp" name="pp" cols="60" rows="10"><%=O.hv("comment.pp")%></textarea>
</p>

<p>
    <%=F.label("qq")%><br />
  <textarea id="comment_qq" name="qq" cols="60" rows="10"><%=O.hv("comment.qq")%></textarea>
</p>

<p>
    <%=F.label("rr")%><br />
  <textarea id="comment_rr" name="rr" cols="60" rows="10"><%=O.hv("comment.rr")%></textarea>
</p>

<p>
    <%=F.label("ss")%><br />
  <textarea id="comment_ss" name="ss" cols="60" rows="10"><%=O.hv("comment.ss")%></textarea>
</p>

<p>
    <%=F.label("tt")%><br />
  <textarea id="comment_tt" name="tt" cols="60" rows="10"><%=O.hv("comment.tt")%></textarea>
</p>

<p>
    <%=F.label("uu")%><br />
  <textarea id="comment_uu" name="uu" cols="60" rows="10"><%=O.hv("comment.uu")%></textarea>
</p>

<p>
    <%=F.label("vv")%><br />
  <textarea id="comment_vv" name="vv" cols="60" rows="10"><%=O.hv("comment.vv")%></textarea>
</p>

<p>
    <%=F.label("ww")%><br />
  <textarea id="comment_ww" name="ww" cols="60" rows="10"><%=O.hv("comment.ww")%></textarea>
</p>

<p>
    <%=F.label("xx")%><br />
  <textarea id="comment_xx" name="xx" cols="60" rows="10"><%=O.hv("comment.xx")%></textarea>
</p>

<p>
    <%=F.label("yy")%><br />
  <textarea id="comment_yy" name="yy" cols="60" rows="10"><%=O.hv("comment.yy")%></textarea>
</p>

<p>
    <%=F.label("zz")%><br />
  <textarea id="comment_zz" name="zz" cols="60" rows="10"><%=O.hv("comment.zz")%></textarea>
</p>

<p>
    <%=F.label("a1")%><br />
  <textarea id="comment_a1" name="a1" cols="60" rows="10"><%=O.hv("comment.a1")%></textarea>
</p>

<p>
    <%=F.label("a2")%><br />
  <textarea id="comment_a2" name="a2" cols="60" rows="10"><%=O.hv("comment.a2")%></textarea>
</p>

<p>
    <%=F.label("a3")%><br />
  <textarea id="comment_a3" name="a3" cols="60" rows="10"><%=O.hv("comment.a3")%></textarea>
</p>

<p>
    <%=F.label("a4")%><br />
  <textarea id="comment_a4" name="a4" cols="60" rows="10"><%=O.hv("comment.a4")%></textarea>
</p>


  <input id="comment_submit" name="commit" type="submit" value="Create" />&nbsp;&nbsp;&nbsp;<input type="reset"/>
<%=F.formForClose("comments")%>

<br />

<%=W.labelLink("List", R.resourcePath("comments"))%>|
<%=W.labelLink("Paged List", R.resourcePath("comments") + "?paged=true")%>
