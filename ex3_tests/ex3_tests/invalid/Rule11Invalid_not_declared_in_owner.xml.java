class Main {
	public static void main(String[] args) {
		System.out.println(0);
	}
}

class Base {
	int data;

}
class Derived extends Base {
	int set(int x) {
		data = this.doesntExist();
		return data;
	}

}
