package bjs.task32;

public class Main {

    public static void main(String[] args) {
        ComputersStore computersStore = new ComputersStore();
        ComputersStoreLoader storeLoader = new ComputersStoreLoader(computersStore);
        storeLoader.parse("computerStore.xml");

        System.out.print(computersStore);
    }
}

/*
Catalogs in the computer store:
Catalog id = 1
	Id      Title           Type            Amount
	1       Computer1       Desktop         10
	2       Computer2       Laptop          20
	3       Computer3       Tablet          30
 */
