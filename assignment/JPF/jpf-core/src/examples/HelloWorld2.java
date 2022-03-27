/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The Java Pathfinder core (jpf-core) platform is licensed under the
 * Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

class SharedObject {
	long x;
	
	public SharedObject() {
		x = 0;
	}
	
	public void write(long a) {
		x = a;
	}

	public void inc() {
		x++;
	}

	public void dec() {
		x--;
	}
	
	public long read() {
		return x;
	}
	
}

class MyThreadA extends Thread {

	private SharedObject obj;
	
	public MyThreadA(SharedObject obj) {
		this.obj = obj;
	}
	
	public void run() {
		// action1();
		// obj.write(100);
		obj.inc();
	}
	
	private void action1() {
		System.out.println("a");
	}
}

class MyThreadB extends Thread {
	
	private SharedObject obj;
	
	public MyThreadB(SharedObject obj) {
		this.obj = obj;
	}
	
	public void run() {
		// action1();
		// long x = obj.read();
		// System.out.println(x);
		obj.dec();
	}
	
	private void action1() {
		System.out.println("b");
	}
}


/**
 * you guess what that one does - just like a normal VM
 */
public class HelloWorld2 {
  public static void main(String[] args){
	  
	  	SharedObject obj = new SharedObject();
	    Thread t1 = new MyThreadA(obj);
	    t1.start();
	    Thread t2 = new MyThreadB(obj);
	    t2.start();
 
	    try {
		    t1.join();
		    t2.join();
		    long x = obj.read();
		    System.out.println(x);
		    assert x == 0;
	    } catch (InterruptedException ex) {
	    		ex.printStackTrace();
	    }
	    
  }
}
