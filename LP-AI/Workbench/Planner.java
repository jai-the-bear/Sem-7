/* 
            stack(X,Y):  put block X on block Y
            Pre: CLEAR(y)^HOLDING(x)
            Add: ARMEMPTY^On(x,y)^CLEAR(x)
            Del:CLEAR(y)^HOLDING(x)
            
            
            unstack(X,Y): remove block X from block Y
            Pre: On(x,y)^CLEAR(x)^ARMEMPTY()
            Add:HOLDING(x)^CLEAR(y)
            Del:On(x,y)^CLEAR(x)^ARMEMPTY()
            
            
            pickup(X):pick up block X from the table
            Pre:ONTABLE(x)^CLEAR(x)^ARMEMPTY()
            Add:HOLDING(x)
            Del:ONTABLE(x)^CLEAR(x)^ARMEMPTY()
            
            
            
            putdown(X): put block X on the table
            Pre:HOLDING(x)
            Add:ONTABLE(x)^ARMEMPTY^CLEAR(x)
            Del:HOLDING(x)
            
*/

import java.util.*;
class Planner{
	static Stack<String> goalStack = new Stack<>();
	static String start_state="ON(B,A) ^ ONTABLE(A) ^ ONTABLE(C) ^ ONTABLE(D) ^ ARMEMPTY ^ CLEAR(B) ^ CLEAR(C) ^ CLEAR(D)";
	static String goal_state="ON(B,D) ^ ON(C,A) ^ ONTABLE(A) ^ ONTABLE(D) ^ CLEAR(B) ^ CLEAR(C)"; 
	static ArrayList<String> current_state;
	
	
	
	public static void list_operate(ArrayList<String> lp,ArrayList<String> la,ArrayList<String> ld,boolean alltrue){
        if(!alltrue){
            //System.out.println(":::PRE:::");
            for(String as:lp){
           //ADD Preconditions to Goal Stack 
            goalStack.push(as);
	        }
	    }
	    else{
              //System.out.println(":::ADD:::");
              for(String as:la){
	         //System.out.println(as);
	         //IF all preconditons pop add items to state
	                if(!current_state.contains(as.trim())){
	                    current_state.add(as.trim());
	                }
	        }
	          //System.out.println(":::DEL:::");
              for(String as:ld){
	         //System.out.println(as);
	         //IF all preconditons pop, delete items from current state  
	             if(current_state.contains(as.trim())){
	                  current_state.remove(as.trim());
	             }
	          }
	      }
	}
	
	public static void stack(char x,char y,boolean alltrue){
	    String Pre="HOLDING("+x+")^CLEAR("+y+")";
        String Add="ARMEMPTY^On("+x+","+y+")^CLEAR("+x+")";
        String Del="CLEAR("+y+")^HOLDING("+x+")";
        ArrayList<String> lp=list_assertion(Pre);
        ArrayList<String> la=list_assertion(Add);
        ArrayList<String> ld=list_assertion(Del);
        if(!alltrue){
           goalStack.push("STACK("+x+","+y+")");
        }
        list_operate(lp,la,ld,alltrue);
	}
	public static void unstack(char x,char y,boolean alltrue){
        String Pre="ON("+x+","+y+")^CLEAR("+x+")^ARMEMPTY";
        String Add="HOLDING("+x+")^CLEAR("+y+")";
        String Del="ON("+x+","+y+")^CLEAR("+x+")^ARMEMPTY";    
        ArrayList<String> lp=list_assertion(Pre);
        ArrayList<String> la=list_assertion(Add);
        ArrayList<String> ld=list_assertion(Del);
        if(!alltrue){
           goalStack.push("UNSTACK("+x+","+y+")");
        }
        list_operate(lp,la,ld,alltrue);   
	}
	public static void pickup(char x,boolean alltrue){
	    String Pre="ONTABLE("+x+")^CLEAR("+x+")^ARMEMPTY";
        String Add="HOLDING("+x+")";
        String Del="ONTABLE("+x+")^CLEAR("+x+")^ARMEMPTY";          
        ArrayList<String> lp=list_assertion(Pre);
        ArrayList<String> la=list_assertion(Add);
        ArrayList<String> ld=list_assertion(Del);
        if(!alltrue){
           goalStack.push("PICKUP("+x+")");
        }
        list_operate(lp,la,ld,alltrue);   
	}
	public static void putdown(char x,boolean alltrue){
	    String Pre="HOLDING("+x+")";
        String Add="ONTABLE("+x+")^ARMEMPTY^CLEAR("+x+")";
        String Del="HOLDING("+x+")";
        ArrayList<String> lp=list_assertion(Pre);
        ArrayList<String> la=list_assertion(Add);
        ArrayList<String> ld=list_assertion(Del);
        if(!alltrue){
           goalStack.push("PUTDOWN("+x+")");
        }
        list_operate(lp,la,ld,alltrue);   
	}
	public static ArrayList<String> list_assertion(String compound){
	    ArrayList<String> maker =new ArrayList<>();
	    String assertion[] = compound.split("['^']");
	    for(String as:assertion){
	        //System.out.println(sg.trim());
	      maker.add(as.trim());
	    }
	    return maker;
	}
	
	public static void main(String args[]){
	
	    //List of Assertions that are True which make the state description
        current_state=list_assertion(start_state);
	    
	    //Push Final State and SubGoals onto Stack
	    goalStack.push(goal_state);
	    String subgoals[] = goal_state.split("['^']");
	    for(String sg:subgoals){
	      goalStack.push(sg.trim());
	    }
	    
	    
	      //stack('a','b',false);
	    show_stack();
	    char t='\0';
	    char x='\0';
	    char y='\0';
	    String temp="";
	     while(!goalStack.empty()){
	         String top=goalStack.pop();
	         //System.out.println("Check if "+top+" holds true");
	         if(current_state.contains(top)){ 
	            System.out.println(top+" True pushed off Stack");
	            show_stack();
	         }
	         else{
	            if(top.contains("ONTABLE")){
	                x=top.charAt(8);
	                putdown(x,false);
	            }
	            else if(top.contains("ON")){
	                x=top.charAt(3);
	                y=top.charAt(5);
	                stack(x,y,false);
	                System.out.println(top+" Replaced");
	                show_stack();
	            }
	            else if(top.contains("ARMEMPTY")){
	                //check for holding and put down OR STACK 
	                for(String assertion:current_state){
	                    if(assertion.contains("HOLDING")){
	                        temp=assertion;
	                    }
	                }
	                x=temp.charAt(8);
	                putdown(x,false);
	                
	            }
	            else if(top.contains("HOLDING")){
	                x=top.charAt(8);
	                if(current_state.contains("ONTABLE("+x+")")){
	                    pickup(x,false);
	                }
	                else{
	                    for(String assertion:current_state){
	                        if(assertion.contains("ON("+x+",")){
	                            temp=assertion;
	                        }
	                    }
	                   y=temp.charAt(5);
	                   unstack(x,y,false); 
	                }
	            }
	            else if(top.contains("CLEAR")){
	                t=top.charAt(6);
	                //look for on(?,t)
	                //unstack(?,t)         
	            }
	            else if(top.contains("UNSTACK")){
	                x=top.charAt(8);
	                y=top.charAt(10);
	                unstack(x,y,true);
	            }
	            else if(top.contains("STACK")){
	                x=top.charAt(6);
	                y=top.charAt(8);
	                stack(x,y,true);
	            }
	            else if(top.contains("PICKUP")){
	                x=top.charAt(7);
	                pickup(x,true);
	            }
	            else if(top.contains("PUTDOWN")){
	                x=top.charAt(8);
	                putdown(x,true);
	            }
	           else{goalStack.push(top);}
	         }
	         
	        //System.out.println(goalStack.pop());   
	     }
	     
	   // show_stack();
	    //	    show_stack();
	}
	
	
	public static void show_stack(){
	
	    System.out.println("-----------");
	    Stack<String> tStack = new Stack<>();
	    String temp="";
	    while(!goalStack.empty()){
	     temp=goalStack.pop();   
	     System.out.println(temp);
	     tStack.push(temp);   
	    }
	    
	    while(!tStack.empty()){
	     goalStack.push(tStack.pop());     
	    }
	 
	    System.out.println("-----------");   
	}
}