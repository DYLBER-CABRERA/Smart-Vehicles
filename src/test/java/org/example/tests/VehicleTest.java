package org.example.tests;
import org.example.models.Combustion_Vehicle;
import org.example.models.Electric_Vehicle;
import org.example.models.Hybrid_Vehicle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/* Nota: para ejecutar el archivo de VehicleTest en la terminal se coloca -> mvn test
  */

public class VehicleTest {
    // Test método verificar si encendió el motor correctamente en vehículos eléctricos
    @Test
    void testStartEngine_Success() {
        // Crea una instancia de Electric_Vehicle con carga suficiente con la carga inicial
        Electric_Vehicle ev = new Electric_Vehicle("123ABC", "Tesla", "Model S", 2022, 100, 80, 50);

        /*Llama al método startEngine() para encender el motor. Devuelve true si el motor
         se enciende correctamente y descuenta 0.1 kWh de la batería.*/
        boolean resultado = ev.startEngine();
        assertTrue(resultado);

        // Verifica que la carga haya disminuido según lo esperado en el caso disminuye en 0.1
        assertEquals(49.9, ev.getCurrent_charge(), "fallo los valores no cohinciden");
    }

    // Test método verificar si el tanque se llena correctamente en vehículos híbridos
    @Test
    void testRefuel_FullTank(){
        // Crea una instancia de vehículo de combustion con carga completa
        Combustion_Vehicle cv = new Combustion_Vehicle("456DEF", "Toyota", "Prius", 2021, 180, 40);

        // Llama al método bunker() para intentar recargar la batería
        cv.bunker();

        // Verifica que el tanque esté lleno
        assertEquals(cv.getFuel_level(), cv.getTank_capacity(), "El tanque no se llenó correctamente");

    }

    // Test método verificar si el piloto automático se activa correctamente en vehículos híbridos
    @Test
    void testAuto_Pilot(){
        // crear instancia de vehículo hibrido
        Hybrid_Vehicle hv = new Hybrid_Vehicle("789GHI", "Ford", "Fusion", 2020, 200, 50);

        // llamamos al método de piloto Automático
        hv.autoPilot();

        // Verificamos que el piloto automático se haya activado correctamente
        assertTrue(hv.isAutopilot_active(), "El piloto automático no se activó correctamente");
    }


}
