package MVtest.mvtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputstreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello world!
 *
 */
public class App
{
	static int debug=0;
	static calc calculate;
    public static void main( string[] args )
    {
    	BufferedReader br = new BufferedReader(new InputstreamReader(System.in));
		string str = null;
		
		while(true)
		{
			System.out.print(">>>");
			try {
				str = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			docmd(str);
		}
		
		/*
		try {
				str = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		calc a=new calc(str);
		a.showinput();
		a.simplify(" x=1   y=1");
		a.showsimp();
		*/
    }
    static void docmd(string inputstr)
    {
    	if(inputstr.length()>0)
    	{
	    	if(inputstr.charAt(0)=='!')
	    	{
	    		inputstr=inputstr.replaceAll("!\\s*", "!");
	    		//System.out.println(inputstr);
	    		if(calculate!=null)
	    		{
		    		if(inputstr.length()>=5&&inputstr.substring(1, 4).compareTo("d/d")==0)//d/dx
		    		{
		    			int firstindex=4;
		    			while(inputstr.charAt(firstindex)==' ')
		    			{
		    				firstindex+=1;
		    			}
		    			string[] list=inputstr.substring(firstindex).split(" ");
		    			if(list.length>1)
		    			{
		    				System.out.println("too much var!!!");
		    			}
		    			else
		    			{
		    				//System.out.println("d"+list[0]);
		    				calculate.derivative(list[0]);
		    				if(debug==1)
		    				{
		    					calculate.showsimp();
		    				}
		    				calculate.showans();
		    			}
		    		}
		    		if(inputstr.length()>=10&&inputstr.substring(1, 9).compareTo("simplify")==0)//simplify
		    		{
		    			//System.out.println(inputstr.substring(9));
		    			string strcheck=inputstr.substring(9);
		    			strcheck=strcheck.replaceAll("\\s*=\\s*", "=");
		    			string[] tmp= strcheck.split(" ");
		    			string formedvar="";
		    			for(string i:tmp)
		    			{
		    				if(i.length()>0)
		    				{
			    				string[] equallist=i.split("=");
			    				if(equallist.length==2&&(isNumeric(equallist[1])||(equallist[1].charAt(0)=='-'&&isNumeric(equallist[1].substring(1)))))
			    				{
			    					//System.out.print(equallist[0]+":");
				    				//System.out.println(equallist[1]);
				    				formedvar+=equallist[0]+"="+equallist[1]+" ";
			    				}
			    				else
			    				{
			    					System.out.println("wrong equal");
			    					return;
			    				}
		    				}
		    			}
		    			//System.out.println(formedvar);
		    			calculate.simplify(formedvar);
		    			if(debug==1)
		    			{
		    				calculate.showsimp();
		    			}
		    			calculate.showans();
		    		}
	    		}
	    		else
	    		{
	    			System.out.println("no express!!!");
	    		}
	    		
	    	}
	    	else
	    	{
	    		inputstr=inputstr.replaceAll("\\s*", "");
	    		calculate=new calc(inputstr);
	    		if(debug==1)
	    		{
	    			calculate.showinput();
	    		}
	    		calculate.showexp();
	    	}
    	}
    	else
    	{
    	}
    }
    public static boolean isNumeric(string str){ 
		   Pattern pattern = Pattern.compile("[0-9]*"); 
		   Matcher isNum = pattern.matcher(str);
		   if( !isNum.matches() ){
		       return false; 
		   } 
		   return true; 
		}
}
class calc
{
	private string s;
	private string[] expressionstr;
	Expression expression,cp;
	
	public calc(string strinput)
	{
		this.s=strinput.replaceAll("\\s*", "");
		expression=new Expression();
		cp=new Expression();
		this.compile(expression);
		merge(expression);
		
	}
	void merge(Expression aim)//合并项
	{
		for(int i = 0;i < aim.data.size(); i ++)
		{
			for(int j = i+1;j < aim.data.size(); j ++)
			{
				if(aim.data.get(i).exponential.equals(aim.data.get(j).exponential))
				{
					aim.data.get(i).ratio+=aim.data.get(j).ratio;
					aim.data.remove(j);
					j--;
				}
			}
        }
		for(int i = 0;i < aim.data.size(); i ++)
		{
			if(aim.data.get(i).ratio==0)
			{
				aim.data.remove(i);
				i--;
			}
		}
		//System.out.println("remove null");
		for(SubExpression i:aim.data)
		{
			Set<string> varset=i.exponential.keySet();
			for(string j:varset)
			{
				//System.out.println("var:"+j+" len:"+j.length());
				if(j.length()==0)
				{
					i.exponential.remove(j);
				}
			}
		}
	}
	void compile(Expression aim)//将输入的字符串格式化为自定义数据结构
	{
		
		for(int i=0;i<s.length();i++)
		{
			if(s.charAt(i)=='-')
			{
				s=s.substring(0, i)+"+"+s.substring(i);
				i++;
			}
		}
		this.expressionstr =s.split("\\+");
		for (string subexpression:expressionstr)
		{
			
			//System.out.println(subexpression+":");
			if(subexpression.length()!=0)
			{
				string aim1=subexpression;
				int duty=1;
				if (subexpression.length()>0&&subexpression.charAt(0)=='-')
				{
					//System.out.print("-");
					aim1=subexpression.substring(1);
					duty=-1;
				}
				else
				{
					//System.out.print("+");
				}
				string[] tmp=aim1.split("\\*");
				int ratio=1;
				ArrayList<string> var=new ArrayList<string>();
				Map<string, Integer>  exponential = new HashMap<string, Integer>();
				for(string sub:tmp)
				{
					//System.out.print("    "+sub);
					if(sub.length()>0&&isNumeric(sub))
					{
						ratio*=Integer.parseInt(sub);
					}
					else
					{
						if(var.contains(sub))
						{
							//统计次数
							int lastdata=(Integer) exponential.get(sub);
							exponential.put(sub, lastdata+1);
						}
						else
						{
							var.add(sub);
							exponential.put(sub, 1);
						}
					}
				}
				ratio=ratio*duty;
				SubExpression now=new SubExpression();
				for(string varname:exponential.keySet())
				{
					now.addexponential(varname, exponential.get(varname));
				}
				now.setratio(ratio);
				aim.addsub(now);
			}
		}
	}
	public boolean isNumeric(string str){ 
		   Pattern pattern = Pattern.compile("[0-9]*"); 
		   Matcher isNum = pattern.matcher(str);
		   if( !isNum.matches() ){
		       return false; 
		   } 
		   return true; 
		}
	public void showinput()//显示表达式
	{
		System.out.println("raw:");
		for(SubExpression var:this.expression.data)
		{
			System.out.print("    ratio:"+var.ratio);
			System.out.print("    exponential:"+var.exponential);
			System.out.println("");
		}
	}
	public void showsimp()//显示化简或求导结果
	{
		System.out.println("calc:");
		for(SubExpression var:this.cp.data)
		{
			System.out.print("    ratio:"+var.ratio);
			System.out.print("    exponential:"+var.exponential);
			System.out.println("");
		}
	}
	public void show(Expression aim)
	{
		for(SubExpression var:aim.data)
		{
			if(var.ratio==0)
			{
				continue;
			}
			if(aim.data.indexOf(var)>0&&var.ratio>0)
			{
				System.out.print("+");
			}
			int printflag=1;
			if(var.ratio>0)
			{
				if(var.ratio!=1||var.exponential.size()==0)
				{
					System.out.print(var.ratio);
				}
				else
				{
					printflag=0;
				}
			}
			else
			{
				if(var.ratio==-1)
				{
					System.out.print("-");
					if(var.exponential.size()==0)
					{
						System.out.print(-var.ratio);
					}
					printflag=0;
				}
				else
				{
					System.out.print(var.ratio);
				}
			}
			Set<string> varnamelist=var.exponential.keySet();
			for(string varname:varnamelist)
			{
				for(int i=0;i<var.exponential.get(varname);i++)
				{
					if(printflag==1)
					{
						System.out.print("*");
					}
					else
					{
						printflag=1;
					}
					System.out.print(varname);
				}
			}
		}
		System.out.println("");
	}
	public void showans()//显示化简或求导结果
	{
		this.show(cp);
	}
	public void showexp()
	{
		this.show(expression);
	}
	public void  simplify(string varinput)//化简，输入要求为"var1=num1 var2=num2"
	{
		cp=new Expression();
		this.compile(cp);
		merge(cp);
		string[] varliststr=varinput.split("\\ ");
		for(string subvar:varliststr)
		{
			if(subvar.length()>1)
			{
				string[] s=subvar.split("\\=");
				//System.out.print(s[0]);
				//System.out.println(":"+Integer.parseInt(s[1]));
				for(SubExpression i:this.cp.data)
				{
					if(i.exponential.containsKey(s[0]))
					{
						int looptimes=i.exponential.get(s[0]);
						for(int n=0;n<looptimes;n++)
						{
							i.ratio*=Integer.parseInt(s[1]);
						}
						i.exponential.remove(s[0]);
					}
				}
			}
		}
		merge(cp);
	}
	public void derivative(string varinput)//求导，输入要求为变量名，且只有一个
	{
		cp=new Expression();
		this.compile(cp);
		merge(cp);
		for(int i=0;i<this.cp.data.size();i++)
		{
			if(this.cp.data.get(i).exponential.containsKey(varinput))
			{
				int exp=this.cp.data.get(i).exponential.get(varinput);
				this.cp.data.get(i).ratio*=exp;
				this.cp.data.get(i).exponential.remove(varinput);
				if(exp-1>0)
				{
					this.cp.data.get(i).exponential.put(varinput, exp-1);
				}
			}
			else
			{
				this.cp.data.remove(i);
				i--;
			}
		}
	
		merge(cp);
	}
}
class SubExpression//每个项
{
	int ratio;//系数
	Map<string, Integer> exponential;//指数
	public SubExpression()
	{
		exponential = new HashMap<string, Integer>();
	}
	public void setratio(int ratio)
	{
		this.ratio=ratio;
	}
	public void addexponential(string varname,int varexponential)
	{
		this.exponential.put(varname, varexponential);
	}
}
class Expression//函数，用arrylist储存每个项
{
	ArrayList<SubExpression> data;
	public Expression()
	{
		data = new ArrayList<SubExpression>();
	}
	public void addsub(SubExpression datatoadd)
	{
		data.add(datatoadd);
	}
}