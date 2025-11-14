package com.builderssas.api.config;

import com.builderssas.api.domain.model.construction.ConstructionType;
import com.builderssas.api.domain.model.construction.ConstructionTypeMaterial;
import com.builderssas.api.domain.model.material.MaterialType;
import com.builderssas.api.domain.model.project.Project;
import com.builderssas.api.domain.model.enums.ProjectStatus;
import com.builderssas.api.domain.model.user.Role;
import com.builderssas.api.domain.model.user.User;
import com.builderssas.api.domain.model.user.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class DataInitializer {

    @PersistenceContext
    private EntityManager em;

    private static final String STOP = "STOP_DATA_INIT";

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initData() {
        try {

            // ============================================================
            // ✅ BLOQUE FUNCIONAL SIN IF Y SIN IMPERATIVO
            //    Si ya existe un proyecto ⇒ se detiene la inicialización
            // ============================================================

            Optional.ofNullable(
                    em.createQuery("SELECT p.id FROM Project p", Long.class)
                            .setMaxResults(1)
                            .getResultStream()
                            .findFirst()
                            .orElse(null)
            ).ifPresent(id -> {
                throw new RuntimeException(STOP);
            });

            // ============================================================
            // ✅ TODO TU CÓDIGO ORIGINAL, SIN CAMBIAR NADA
            // ============================================================

            // ✅ ROLES
            Stream.of("ROLE_ADMIN", "ROLE_ARCHITECT", "ROLE_USER")
                    .filter(name -> em.createQuery(
                                    "SELECT COUNT(r) FROM Role r WHERE r.name=:n",
                                    Long.class)
                            .setParameter("n", name)
                            .getSingleResult() == 0)
                    .map(name -> Role.builder().name(name).description(name).build())
                    .forEach(em::persist);

            em.flush();

            Role adminRole = em.createQuery("SELECT r FROM Role r WHERE r.name='ROLE_ADMIN'", Role.class).getSingleResult();
            Role architectRole = em.createQuery("SELECT r FROM Role r WHERE r.name='ROLE_ARCHITECT'", Role.class).getSingleResult();
            Role userRole = em.createQuery("SELECT r FROM Role r WHERE r.name='ROLE_USER'", Role.class).getSingleResult();

            // ✅ USUARIOS
            record U(String username, String firstname, String lastname, String email, String password) {}

            Stream.of(
                            new U("admin", "Carlos", "Ramírez", "admin@test.com", "123456"),
                            new U("arq1", "Juliana", "Lopez", "arq1@test.com", "123456"),
                            new U("user1", "Miguel", "Cruz", "user1@test.com", "123456")
                    )
                    .filter(u -> em.createQuery("SELECT COUNT(x) FROM User x WHERE x.username=:u", Long.class)
                            .setParameter("u", u.username())
                            .getSingleResult() == 0)
                    .map(u -> User.builder()
                            .username(u.username())
                            .firstname(u.firstname())
                            .lastname(u.lastname())
                            .email(u.email())
                            .password(u.password())
                            .active(true)
                            .build())
                    .forEach(em::persist);

            em.flush();

            User uAdmin = em.createQuery("SELECT u FROM User u WHERE u.username='admin'", User.class).getSingleResult();
            User uArq1 = em.createQuery("SELECT u FROM User u WHERE u.username='arq1'", User.class).getSingleResult();
            User uUser1 = em.createQuery("SELECT u FROM User u WHERE u.username='user1'", User.class).getSingleResult();

            // ✅ USER_ROLE
            Stream.of(
                            UserRole.builder().user(uAdmin).role(adminRole).build(),
                            UserRole.builder().user(uArq1).role(architectRole).build(),
                            UserRole.builder().user(uUser1).role(userRole).build(),
                            UserRole.builder().user(uAdmin).role(architectRole).build()
                    )
                    .forEach(em::persist);

            em.flush();

            // ✅ MATERIAL TYPES
            record MT(String code, String name, String unit, Double stock) {}

            Stream.of(
                            new MT("Ce", "Cemento", "kg", 1000.0),
                            new MT("Gr", "Grava",   "kg", 1500.0),
                            new MT("Ar", "Arena",   "kg", 2500.0),
                            new MT("Ma", "Madera",  "unid", 3000.0),
                            new MT("Ad", "Adobe",   "kg", 3500.0)
                    )
                    .filter(mt ->
                            em.createQuery("SELECT COUNT(m) FROM MaterialType m WHERE m.code=:code", Long.class)
                                    .setParameter("code", mt.code())
                                    .getSingleResult() == 0
                    )
                    .map(mt -> MaterialType.builder()
                            .code(mt.code())
                            .name(mt.name())
                            .unit(mt.unit())
                            .stock(mt.stock())
                            .build())
                    .forEach(em::persist);

            em.flush();

            // ✅ CONSTRUCTION TYPES
            Map<String, Integer> ctTypes = Map.of(
                    "HOUSE", 3,
                    "LAKE", 2,
                    "SOCCER_FIELD", 1,
                    "BUILDING", 6,
                    "GYM", 4
            );

            ctTypes.entrySet().stream()
                    .filter(e -> em.createQuery(
                                    "SELECT COUNT(c) FROM ConstructionType c WHERE c.name=:n",
                                    Long.class)
                            .setParameter("n", e.getKey())
                            .getSingleResult() == 0)
                    .map(e -> ConstructionType.builder()
                            .name(e.getKey())
                            .durationDays(e.getValue())
                            .build())
                    .forEach(em::persist);

            em.flush();

            // ✅ CT MATERIALS
            Map<String, Map<String, Double>> recipe = Map.of(
                    "HOUSE", Map.of("Cemento", 100.0, "Grava", 50.0, "Arena", 90.0, "Madera", 20.0, "Adobe", 100.0),
                    "LAKE", Map.of("Cemento", 50.0, "Grava", 60.0, "Arena", 80.0, "Madera", 10.0, "Adobe", 20.0),
                    "SOCCER_FIELD", Map.of("Cemento", 20.0, "Grava", 20.0, "Arena", 20.0, "Madera", 20.0, "Adobe", 20.0),
                    "BUILDING", Map.of("Cemento", 200.0, "Grava", 100.0, "Arena", 180.0, "Madera", 40.0, "Adobe", 200.0),
                    "GYM", Map.of("Cemento", 50.0, "Grava", 25.0, "Arena", 45.0, "Madera", 20.0, "Adobe", 30.0)
            );

            recipe.entrySet().stream()
                    .flatMap(ct -> ct.getValue().entrySet().stream()
                            .map(m -> Map.of(
                                    "ctName", ct.getKey(),
                                    "mtName", m.getKey(),
                                    "qty", m.getValue()
                            ))
                    )
                    .forEach(entry -> {

                        ConstructionType ct = em.createQuery(
                                        "SELECT c FROM ConstructionType c WHERE c.name=:n",
                                        ConstructionType.class)
                                .setParameter("n", entry.get("ctName"))
                                .getSingleResult();

                        MaterialType mt = em.createQuery(
                                        "SELECT m FROM MaterialType m WHERE m.name=:n",
                                        MaterialType.class)
                                .setParameter("n", entry.get("mtName"))
                                .getSingleResult();

                        Long count = em.createQuery("""
                                        SELECT COUNT(ctm) FROM ConstructionTypeMaterial ctm
                                        WHERE ctm.constructionType.id = :ct
                                          AND ctm.materialType.id = :mt
                                        """, Long.class)
                                .setParameter("ct", ct.getId())
                                .setParameter("mt", mt.getId())
                                .getSingleResult();

                        if (count == 0) {
                            em.persist(ConstructionTypeMaterial.builder()
                                    .constructionType(ct)
                                    .materialType(mt)
                                    .quantityRequired((Double) entry.get("qty"))
                                    .build());
                        }
                    });

            em.flush();

            // ✅ PROJECTS
            Stream.of(
                            Project.builder()
                                    .name("Ciudadela del Futuro")
                                    .description("Proyecto urbano integral con 2000 viviendas y zonas verdes.")
                                    .projectStatus(ProjectStatus.PLANNED)
                                    .createdBy(uArq1)
                                    .build(),
                            Project.builder()
                                    .name("Parque Central Metropolitano")
                                    .description("Desarrollo de parque ecológico central en la ciudad.")
                                    .projectStatus(ProjectStatus.PLANNED)
                                    .createdBy(uArq1)
                                    .build(),
                            Project.builder()
                                    .name("Centro Deportivo Norte")
                                    .description("Construcción de un complejo deportivo multifuncional.")
                                    .projectStatus(ProjectStatus.PLANNED)
                                    .createdBy(uAdmin)
                                    .build()
                    )
                    .forEach(em::persist);

            em.flush();

        } catch (RuntimeException ex) {
            // ✅ SILENCIA la excepción funcional sin mostrar errores
            if (STOP.equals(ex.getMessage())) {
                return; // no ejecuta seed, no muestra nada
            }
            throw ex; // otros errores sí deben mostrarse
        }
    }
}
