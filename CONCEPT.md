# Hill's RPG — Konsep Desain

Status: **draft, bertumbuh seiring development.** Ini rencana besar (roadmap), bukan berarti
semuanya harus dikerjakan sebelum mod bisa dites. MVP saat ini ada di bagian paling bawah.

## 1. Visi

Fabric mod RPG class-based untuk MC 1.21.1, dibangun di atas Spell Engine sebagai mesin sihir.
Pemain memilih class, naik level, mengumpulkan gear bertingkat, melawan musuh/boss custom, dan
mengikuti progression cerita ringan.

## 2. Pilar Sistem

### A. Class System
- Class dipilih lewat item "Tome" (satu tome = satu class), sekali pakai.
- Tiap class = spell pool sendiri + starter weapon (spell sudah dibind lewat spell_assignments)
    + stat bonus dasar.
- Rencana class: Mage (magic dps), Warrior (melee tank/dps), Archer (ranged physical dps).
- Class disimpan di command tag (`/tag`) untuk MVP; dimigrasi ke Fabric Data Attachment kalau
  butuh data lebih dari sekadar "sudah pilih class apa belum".

### B. Leveling & Stats
- Attribute custom STR/INT/DEX/VIT, memengaruhi damage spell (lewat spell_power), melee, attack
  speed, dan max health. XP dari membunuh mob & quest.

### C. Equipment Tiers
- Common → Uncommon → Rare → Epic → Legendary, pakai equipment_set Spell Engine untuk set bonus.

### D. Musuh & Boss Custom
- Mob custom yang bisa cast spell sendiri lewat spell container, boss dengan fase.

### E. Quest / Progression Cerita
- Lapisan custom kita sendiri (Spell Engine tidak punya sistem quest), mulai dari Advancement
  API vanilla dulu.

## 3. Prinsip Teknis
- Data-driven dulu, Java secukupnya.
- Satu slice vertikal dulu, baru melebar (jangan bikin 3 class kosong sekaligus).
- Namespace item custom = `hills-rpg`.

## 4. Slice Pertama (SUDAH JALAN)

Scope: **1 class (Mage) + 1 spell (Fireball)**, end-to-end sudah bisa dimainkan.

1. `hills-rpg:mage_tome` — klik kanan sekali → set class Mage + kasih `hills-rpg:mage_wand`.
2. `data/hills-rpg/spell_assignments/mage_wand.json` → bind `hills-rpg:fireball` ke wand.
3. `data/hills-rpg/spell/fireball.json` → contoh teruji dari cheat sheet Spell Engine.
   (catatan: foldernya `spell` singular, bukan `spells` — harus sama dengan nama registry
   yang dipakai di `tags/spell/...`, kalau plural bakal error "missing references")
4. Dependency runtime: Spell Engine, Spell Power, Player Animator, Cloth Config, **Trinkets**.
5. Belum ada: leveling, equipment tier, mob custom, quest, class kedua. Itu iterasi berikutnya
   — lihat bagian "Next Steps" di bawah untuk urutan yang disarankan.

## 5. Next Steps (belum dikerjakan, urutan disarankan)

1. **Spell kedua untuk Mage** — cara paling murah buat nambah "isi" tanpa nyentuh Java sama
   sekali, cukup: 1 file `data/hills-rpg/spell/<nama>.json` baru + tambahkan id-nya ke
   `spell_assignments/mage_wand.json` (array `spell_ids`, boleh lebih dari satu spell per item)
   dan/atau `tags/spell/spell_book/mage.json`.
2. **Class kedua (Warrior/Archer)** — karena `ClassTomeItem` & `ClassManager` sudah generic,
   ini tinggal: 1 item starter baru di `ModItems.java`, 1 baris `ClassTomeItem(...)` baru,
   1 spell/skill baru, dan lang key `hills-rpg.class.<nama>.chosen`. Tidak perlu Item subclass
   baru.
3. **Equipment tier** — baru masuk akal setelah ada >1 class, supaya ada variasi gear yang
   membedakan tiap class.
4. **Leveling/stat & mob/boss custom & quest** — disimpan buat setelah struktur class+item+spell
   terasa stabil dan enak dites, karena ini bagian yang paling banyak butuh kode Java baru.