package com.fleet;

import com.fleet.model.*;
import com.fleet.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Inserts a few sample rows into an empty database so the screens are not blank on first start. */
@Component
@ConditionalOnProperty(name = "fleet.seed-demo", havingValue = "true")
public class DemoDataSeeder implements CommandLineRunner {

    private final CarRepository cars;
    private final DriverRepository drivers;
    private final PersonnelRepository personnel;

    public DemoDataSeeder(CarRepository cars, DriverRepository drivers, PersonnelRepository personnel) {
        this.cars = cars;
        this.drivers = drivers;
        this.personnel = personnel;
    }

    @Override
    public void run(String... args) {
        if (cars.count() > 0 || drivers.count() > 0 || personnel.count() > 0) return;

        car("12345-A-6", 4, "Berline", CarCategory.VIP);
        car("23456-B-6", 7, "4x4", CarCategory.SERVICE_CLASS);
        car("34567-A-1", 5, "Berline", CarCategory.SERVICE_NORMALE);
        car("45678-D-6", 3, "Fourgonnette", CarCategory.MATERIEL);

        driver("Youssef El Amrani", "0661000001", "P-100234");
        driver("Hassan Bennani", "0661000002", "P-100871");

        person("Directeur Général", "0662000001", PersonnelCategory.VIP);
        person("Samira Alaoui", "0662000002", PersonnelCategory.EMPLOYEE);
        person("Invité Mission Paris", "0662000003", PersonnelCategory.GUEST);
    }

    private void car(String mat, int places, String type, CarCategory cat) {
        Car c = new Car();
        c.matricule = mat;
        c.places = places;
        c.type = type;
        c.category = cat;
        cars.save(c);
    }

    private void driver(String name, String phone, String lic) {
        Driver d = new Driver();
        d.name = name;
        d.phone = phone;
        d.licenseNumber = lic;
        drivers.save(d);
    }

    private void person(String name, String phone, PersonnelCategory cat) {
        Personnel p = new Personnel();
        p.name = name;
        p.phone = phone;
        p.category = cat;
        personnel.save(p);
    }
}
