package com.artinkhodro.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.artinkhodro.app.data.AppDatabase
import com.artinkhodro.app.data.Vehicle
import com.artinkhodro.app.data.VehicleDao
import com.artinkhodro.app.data.TechnicalDao
import kotlinx.coroutines.launch

private val Black = Color(0xFF101114)
private val Panel = Color(0xFF1C1E23)
private val Gold = Color(0xFFD6AD55)
private val Soft = Color(0xFFE8E5DE)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.get(this)
        setContent { ArtinApp(database.vehicleDao(), database.technicalDao(), database.vehicleTechnicalDao()) }
    }
}

private data class NavItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtinApp(vehicleDao: VehicleDao, technicalDao: TechnicalDao, vehicleTechnicalDao: com.artinkhodro.app.data.VehicleTechnicalDao) {
    var selected by remember { mutableStateOf("خانه") }
    var selectedVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var showAddVehicle by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val navItems = listOf(
        NavItem("خانه", Icons.Default.Home),
        NavItem("خودروها", Icons.Default.DirectionsCar),
        NavItem("آموزش", Icons.Default.MenuBook),
        NavItem("عیب‌یابی", Icons.Default.Build),
        NavItem("کتابخانه فنی", Icons.Default.Folder),
        NavItem("مالتی‌پلکس", Icons.Default.Build),
        NavItem("جستجو", Icons.Default.Search)
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(colorScheme = darkColorScheme(primary = Gold, background = Black, surface = Panel)) {
            Scaffold(
                containerColor = Black,
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text("ARTIN KHODRO", color = Gold, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                                Text("مرجع تخصصی آموزش و تعمیرات خودرو", color = Soft, fontSize = 11.sp)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Black)
                    )
                },
                bottomBar = {
                    NavigationBar(containerColor = Panel) {
                        navItems.forEach { item ->
                            NavigationBarItem(
                                selected = selected == item.title,
                                onClick = { selected = item.title },
                                icon = { Icon(item.icon, contentDescription = item.title) },
                                label = { Text(item.title, fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Gold,
                                    selectedTextColor = Gold,
                                    indicatorColor = Black,
                                    unselectedTextColor = Soft
                                )
                            )
                        }
                    }
                }
            ) { padding ->
                when (selected) {
                    "خودروها" -> if (selectedVehicle == null) VehicleScreen(vehicleDao, Modifier.padding(padding), { showAddVehicle = true }, { selectedVehicle = it }) else VehicleTechnicalScreen(selectedVehicle!!, vehicleTechnicalDao, technicalDao, Modifier.padding(padding), { selectedVehicle = null })
                    "آموزش" -> TrainingScreen(Modifier.padding(padding))
                    "عیب‌یابی" -> DiagnosisScreen(technicalDao, Modifier.padding(padding))
                    "کتابخانه فنی" -> DocumentsScreen(technicalDao, Modifier.padding(padding))
                    "مالتی‌پلکس" -> MultiplexScreen(Modifier.padding(padding))
                    "جستجو" -> GlobalSearchScreen(vehicleDao, technicalDao, Modifier.padding(padding))
                    else -> HomeScreen(
                        modifier = Modifier.padding(padding),
                        onVehicles = { selected = "خودروها" },
                        onTraining = { selected = "آموزش" },
                        onDiagnosis = { selected = "عیب‌یابی" },
                        onDocuments = { selected = "کتابخانه فنی" }
                    )
                }
            }
            if (showAddVehicle) {
                AddVehicleDialog(
                    onDismiss = { showAddVehicle = false },
                    onSave = { make, model, year ->
                        scope.launch {
                            vehicleDao.insert(Vehicle(make = make, model = model, yearFrom = year))
                        }
                        showAddVehicle = false
                    }
                )
            }
        }
    }
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    onVehicles: () -> Unit,
    onTraining: () -> Unit,
    onDiagnosis: () -> Unit,
    onDocuments: () -> Unit
) {
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp)) {
            Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.artin_logo),
                    contentDescription = "نشان آرتین خودرو",
                    modifier = Modifier.fillMaxWidth().height(210.dp),
                    contentScale = ContentScale.Fit
                )
                Text("دانش فنی، عیب‌یابی دقیق، تعمیر اصولی", color = Soft, fontSize = 12.sp)
            }
        }
        Text("دسترسی سریع", color = Soft, fontWeight = FontWeight.Bold)
        MenuCard("پرونده خودروها", "ثبت و جست‌وجوی خودروها", Icons.Default.DirectionsCar, onVehicles)
        MenuCard("آموزش تخصصی", "برق خودرو، سنسورها و ECU", Icons.Default.MenuBook, onTraining)
        MenuCard("عیب‌یابی و ECU", "بانک خطاها و اطلاعات تعمیراتی", Icons.Default.Build, onDiagnosis)
        MenuCard("کتابخانه فنی", "راهنماها و مستندات مجاز آفلاین", Icons.Default.Folder, onDocuments)
        Text("اطلاعات فنی وابسته به تیپ و شماره سخت‌افزار باید با منبع معتبر تطبیق داده شوند.", color = Gold, fontSize = 12.sp)
    }
}

@Composable
private fun MenuCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, action: () -> Unit) {
    Card(onClick = action, colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Gold, modifier = Modifier.size(30.dp))
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = Soft, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.LightGray, fontSize = 11.sp)
            }
            Icon(Icons.Default.ChevronLeft, null, tint = Gold)
        }
    }
}

@Composable
private fun VehicleScreen(dao: VehicleDao, modifier: Modifier, onAdd: () -> Unit, onSelect: (Vehicle) -> Unit) {
    var query by remember { mutableStateOf("") }
    val vehicles by (if (query.isBlank()) dao.observeAll() else dao.search(query)).collectAsState(initial = emptyList())
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(), label = { Text("جست‌وجوی خودرو") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("پرونده خودروها", color = Soft, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Button(onClick = onAdd, colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Black)) { Icon(Icons.Default.Add, null); Text("افزودن") }
        }
        if (vehicles.isEmpty()) EmptyState("خودرویی ثبت نشده است. برای شروع از افزودن استفاده کنید.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vehicles, key = { it.id }) { vehicle ->
                Card(onClick = { onSelect(vehicle) }, colors = CardDefaults.cardColors(containerColor = Panel)) {
                    Column(Modifier.fillMaxWidth().padding(14.dp)) {
                        Text("${vehicle.make} ${vehicle.model}", color = Gold, fontWeight = FontWeight.Bold)
                        Text("سال: ${vehicle.yearFrom ?: "نامشخص"} تا ${vehicle.yearTo ?: "نامشخص"} | موتور: ${vehicle.engine.ifBlank { "ثبت نشده" }}", color = Soft, fontSize = 12.sp)
                        if (vehicle.ecu.isNotBlank()) Text("ECU: ${vehicle.ecu}", color = Soft, fontSize = 12.sp)
                        Text("برای مشاهده پرونده فنی لمس کنید  ←", color = Gold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun VehicleTechnicalScreen(
    vehicle: Vehicle,
    dao: com.artinkhodro.app.data.VehicleTechnicalDao,
    technicalDao: TechnicalDao,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val sensors by dao.sensorsForVehicle(vehicle.id).collectAsState(initial = emptyList())
    val parameters by dao.parametersForVehicle(vehicle.id).collectAsState(initial = emptyList())
    val fuses by dao.fusesForVehicle(vehicle.id).collectAsState(initial = emptyList())
    val faults by dao.faultCodesForVehicle(vehicle.id).collectAsState(initial = emptyList())
    val repairs by technicalDao.repairsForVehicle(vehicle.id).collectAsState(initial = emptyList())
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت", tint = Gold) }
            Column {
                Text("پرونده فنی خودرو", color = Gold, fontWeight = FontWeight.Bold)
                Text("${vehicle.make} ${vehicle.model} | ${vehicle.engine.ifBlank { "موتور ثبت نشده" }}", color = Soft, fontSize = 12.sp)
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { Text("سنسورها (${sensors.size})", color = Gold, fontWeight = FontWeight.Bold) }
            if (sensors.isEmpty()) item { EmptyState("اطلاعات سنسور اختصاصی این خودرو هنوز وارد نشده است.") }
            items(sensors, key = { "sensor-${it.id}" }) { item ->
                Card(colors = CardDefaults.cardColors(containerColor = Panel)) { Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    Text(item.nameFa, color = Soft, fontWeight = FontWeight.Bold)
                    if (item.locationFa.isNotBlank()) Text("محل نصب: ${item.locationFa}", color = Soft, fontSize = 12.sp)
                    if (item.connectorFa.isNotBlank()) Text("سوکت: ${item.connectorFa}", color = Soft, fontSize = 12.sp)
                    if (item.testMethodFa.isNotBlank()) Text("روش تست: ${item.testMethodFa}", color = Soft, fontSize = 12.sp)
                    Text("وضعیت: ${item.verificationStatus}", color = Gold, fontSize = 10.sp)
                } }
            }
            item { Text("پارامترهای دیاگ (${parameters.size})", color = Gold, fontWeight = FontWeight.Bold) }
            if (parameters.isEmpty()) item { EmptyState("پارامترهای تأییدشده برای این خودرو هنوز ثبت نشده‌اند.") }
            items(parameters, key = { "parameter-${it.id}" }) { item ->
                Card(colors = CardDefaults.cardColors(containerColor = Panel)) { Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    Text(item.nameFa, color = Soft, fontWeight = FontWeight.Bold)
                    Text("واحد: ${item.unit.ifBlank { "ثبت نشده" }} | محدوده: ${item.normalRangeFa.ifBlank { "ثبت نشده" }}", color = Soft, fontSize = 12.sp)
                    if (item.conditionsFa.isNotBlank()) Text(item.conditionsFa, color = Soft, fontSize = 12.sp)
                    Text("وضعیت: ${item.verificationStatus}", color = Gold, fontSize = 10.sp)
                } }
            }
            item { Text("فیوزها و رله‌ها (${fuses.size})", color = Gold, fontWeight = FontWeight.Bold) }
            if (fuses.isEmpty()) item { EmptyState("اطلاعات فیوز و رله اختصاصی این خودرو هنوز ثبت نشده است.") }
            items(fuses, key = { "fuse-${it.id}" }) { item ->
                Card(colors = CardDefaults.cardColors(containerColor = Panel)) { Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    Text("${item.locationFa} — ${item.fuseNumber.ifBlank { "شماره نامشخص" }}", color = Soft, fontWeight = FontWeight.Bold)
                    Text("آمپر: ${item.amperage?.toString() ?: "ثبت نشده"} | عملکرد: ${item.functionFa.ifBlank { "ثبت نشده" }}", color = Soft, fontSize = 12.sp)
                    Text("وضعیت: ${item.verificationStatus}", color = Gold, fontSize = 10.sp)
                } }
            }
            item { Text("کدهای خطای مرتبط (${faults.size})", color = Gold, fontWeight = FontWeight.Bold) }
            if (faults.isEmpty()) item { EmptyState("هنوز کد خطایی به این خودرو تخصیص داده نشده است.") }
            items(faults, key = { "fault-${it.id}" }) { item ->
                Card(colors = CardDefaults.cardColors(containerColor = Panel)) { Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    Text("${item.code} — ${item.titleFa}", color = Soft, fontWeight = FontWeight.Bold)
                    Text(item.descriptionFa, color = Soft, fontSize = 12.sp)
                } }
            }
            item { Text("سوابق تعمیرات ECU (${repairs.size})", color = Gold, fontWeight = FontWeight.Bold) }
            if (repairs.isEmpty()) item { EmptyState("سابقه تعمیر ثبت‌شده‌ای برای این خودرو وجود ندارد.") }
            items(repairs, key = { "repair-${it.id}" }) { item ->
                Card(colors = CardDefaults.cardColors(containerColor = Panel)) { Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    Text(item.symptomFa, color = Soft, fontWeight = FontWeight.Bold)
                    if (item.ecuHardware.isNotBlank()) Text("ECU: ${item.ecuHardware}", color = Soft, fontSize = 12.sp)
                    if (item.diagnosisFa.isNotBlank()) Text("تشخیص: ${item.diagnosisFa}", color = Soft, fontSize = 12.sp)
                    if (item.repairActionFa.isNotBlank()) Text("اقدام: ${item.repairActionFa}", color = Soft, fontSize = 12.sp)
                } }
            }
        }
    }
}

@Composable
private fun AddVehicleDialog(onDismiss: () -> Unit, onSave: (String, String, Int?) -> Unit) {
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ثبت خودرو") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(make, { make = it }, label = { Text("سازنده") }, singleLine = true)
                OutlinedTextField(model, { model = it }, label = { Text("مدل خودرو") }, singleLine = true)
                OutlinedTextField(year, { year = it.filter(Char::isDigit) }, label = { Text("سال ساخت (اختیاری)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
            }
        },
        confirmButton = { TextButton(onClick = {
            val normalizedYear = year.map { ch ->
                when (ch) {
                    '۰' -> '0'; '۱' -> '1'; '۲' -> '2'; '۳' -> '3'; '۴' -> '4'
                    '۵' -> '5'; '۶' -> '6'; '۷' -> '7'; '۸' -> '8'; '۹' -> '9'
                    '٠' -> '0'; '١' -> '1'; '٢' -> '2'; '٣' -> '3'; '٤' -> '4'
                    '٥' -> '5'; '٦' -> '6'; '٧' -> '7'; '٨' -> '8'; '٩' -> '9'
                    else -> ch
                }
            }.joinToString("")
            if (make.isNotBlank() && model.isNotBlank()) onSave(make.trim(), model.trim(), normalizedYear.toIntOrNull())
        }) { Text("ذخیره", color = Gold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}

@Composable
private fun TrainingScreen(modifier: Modifier = Modifier) {
    val topics = listOf(
        "مبانی برق خودرو و عیب‌یابی مدارها",
        "سیستم انژکتوری و مدیریت موتور",
        "شناخت سنسورها و عملگرها",
        "تست مدار با مولتی‌متر و اسیلوسکوپ",
        "ساختار ECU و عیب‌یابی برد",
        "شبکه‌های CAN و LIN و مالتی‌پلکس",
        "سیستم ABS، ESP و ایربگ",
        "گیربکس اتوماتیک و TCU",
        "ایموبیلایزر و کلید هوشمند؛ عیب‌یابی مجاز",
        "ایمنی خودروهای هیبریدی و برقی"
    )
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("آموزش تخصصی", color = Gold, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("سرفصل‌های آموزشی برنامه", color = Soft, fontSize = 12.sp)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(topics) { topic ->
                Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MenuBook, null, tint = Gold)
                        Spacer(Modifier.width(12.dp))
                        Text(topic, color = Soft)
                    }
                }
            }
        }
    }
}

@Composable
private fun DiagnosisScreen(dao: TechnicalDao, modifier: Modifier = Modifier) {
    var query by remember { mutableStateOf("") }
    val codes by dao.searchFaultCodes(query).collectAsState(initial = emptyList())
    val ecus by dao.searchEcus(query).collectAsState(initial = emptyList())
    var expandedCode by remember { mutableStateOf<String?>(null) }
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("عیب‌یابی و بانک ECU", color = Gold, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(), label = { Text("کد خطا، سازنده یا خانواده ECU") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true)
        Text("بانک کدهای خطای عمومی OBD-II", color = Soft, fontWeight = FontWeight.Bold)
        Text("برای مشاهده علت‌ها و مراحل تست، هر کد را باز کنید.", color = Color.LightGray, fontSize = 11.sp)
        if (codes.isEmpty() && ecus.isEmpty()) EmptyState("نتیجه‌ای پیدا نشد. کد را بدون فاصله یا نام خانواده ECU را وارد کنید.")
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(codes, key = { "dtc-${it.id}" }) { item ->
                val expanded = expandedCode == item.code
                Card(
                    onClick = { expandedCode = if (expanded) null else item.code },
                    colors = CardDefaults.cardColors(containerColor = Panel)
                ) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("${item.code} — ${item.titleFa}", color = Gold, fontWeight = FontWeight.Bold)
                                Text(item.descriptionFa, color = Soft, fontSize = 12.sp)
                            }
                            Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = Gold)
                        }
                        if (expanded) {
                            Divider(color = Color.DarkGray)
                            Text("علت‌های احتمالی", color = Gold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(item.possibleCausesFa.ifBlank { "ثبت نشده؛ مطابق مستند سازنده بررسی شود." }, color = Soft, fontSize = 12.sp)
                            Text("مراحل پیشنهادی تست", color = Gold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(item.diagnosticStepsFa.ifBlank { "روش تست اختصاصی خودرو در دسترس نیست." }, color = Soft, fontSize = 12.sp)
                            Text("اعتبار: مرجع عمومی؛ نیازمند تطبیق با خودرو", color = Color.LightGray, fontSize = 10.sp)
                        }
                    }
                }
            }
            if (ecus.isNotEmpty()) {
                item { Text("خانواده‌های ECU", color = Gold, fontWeight = FontWeight.Bold) }
                items(ecus, key = { "ecu-${it.id}" }) { item ->
                    Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
                        Column(Modifier.fillMaxWidth().padding(12.dp)) {
                            Text("${item.manufacturer} — ${item.family}", color = Gold, fontWeight = FontWeight.Bold)
                            Text(item.hardwareReference.ifBlank { "شماره سخت‌افزار ثبت نشده" }, color = Soft, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentsScreen(dao: TechnicalDao, modifier: Modifier = Modifier) {
    var query by remember { mutableStateOf("") }
    val documents by (if (query.isBlank()) dao.observeDocuments() else dao.searchDocuments(query)).collectAsState(initial = emptyList())
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("کتابخانه فنی", color = Gold, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(), label = { Text("جست‌وجوی مستند") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true)
        if (documents.isEmpty()) {
            EmptyState("مستندات فنی هنوز وارد بانک برنامه نشده‌اند. فقط فایل‌های دارای مجوز و تأییدشده به نسخه آفلاین افزوده می‌شوند. بخش نقشه‌ها در این نسخه ارائه نمی‌شود.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(documents) { doc ->
                    Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
                        Column(Modifier.fillMaxWidth().padding(14.dp)) {
                            Text(doc.titleFa, color = Gold, fontWeight = FontWeight.Bold)
                            Text("${doc.category} | ${doc.brand} ${doc.model}", color = Soft, fontSize = 12.sp)
                            Text("اعتبار: ${doc.verificationStatus} | مجوز: ${doc.permissionStatus}", color = Color.LightGray, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}


private fun normalizeSearchTerm(value: String): String = value
    .replace('ي', 'ی').replace('ك', 'ک')
    .map { ch ->
        when (ch) {
            '۰' -> '0'; '۱' -> '1'; '۲' -> '2'; '۳' -> '3'; '۴' -> '4'
            '۵' -> '5'; '۶' -> '6'; '۷' -> '7'; '۸' -> '8'; '۹' -> '9'
            '٠' -> '0'; '١' -> '1'; '٢' -> '2'; '٣' -> '3'; '٤' -> '4'
            '٥' -> '5'; '٦' -> '6'; '٧' -> '7'; '٨' -> '8'; '٩' -> '9'
            else -> ch
        }
    }.joinToString("").trim()

@Composable
private fun GlobalSearchScreen(
    vehicleDao: VehicleDao,
    technicalDao: TechnicalDao,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    val term = normalizeSearchTerm(query)
    val vehicles by (if (term.isBlank()) vehicleDao.observeAll() else vehicleDao.search(term))
        .collectAsState(initial = emptyList())
    val codes by technicalDao.searchFaultCodes(term).collectAsState(initial = emptyList())
    val ecus by technicalDao.searchEcus(term).collectAsState(initial = emptyList())
    val documents by (if (term.isBlank()) technicalDao.observeDocuments() else technicalDao.searchDocuments(term))
        .collectAsState(initial = emptyList())

    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("جستجوی سراسری آرتین خودرو", color = Gold, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        OutlinedTextField(
            query, { query = it }, modifier = Modifier.fillMaxWidth(),
            label = { Text("نام خودرو، کد خطا، ECU یا مستند") },
            leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            if (vehicles.isNotEmpty()) {
                item { Text("خودروها", color = Gold, fontWeight = FontWeight.Bold) }
                items(vehicles, key = { "search-vehicle-${it.id}" }) { v ->
                    Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
                        Column(Modifier.fillMaxWidth().padding(12.dp)) {
                            Text("${v.make} ${v.model}", color = Soft, fontWeight = FontWeight.Bold)
                            Text("موتور: ${v.engine.ifBlank { "ثبت نشده" }} | ECU: ${v.ecu.ifBlank { "ثبت نشده" }}", color = Color.LightGray, fontSize = 11.sp)
                        }
                    }
                }
            }
            if (codes.isNotEmpty()) {
                item { Text("کدهای خطا", color = Gold, fontWeight = FontWeight.Bold) }
                items(codes, key = { "search-dtc-${it.id}" }) { c ->
                    Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
                        Column(Modifier.fillMaxWidth().padding(12.dp)) {
                            Text("${c.code} — ${c.titleFa}", color = Soft, fontWeight = FontWeight.Bold)
                            Text(c.descriptionFa, color = Color.LightGray, fontSize = 11.sp)
                        }
                    }
                }
            }
            if (ecus.isNotEmpty()) {
                item { Text("خانواده‌های ECU", color = Gold, fontWeight = FontWeight.Bold) }
                items(ecus, key = { "search-ecu-${it.id}" }) { e ->
                    Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
                        Column(Modifier.fillMaxWidth().padding(12.dp)) {
                            Text("${e.manufacturer} — ${e.family}", color = Soft, fontWeight = FontWeight.Bold)
                            Text(e.hardwareReference.ifBlank { "شماره سخت‌افزار ثبت نشده" }, color = Color.LightGray, fontSize = 11.sp)
                        }
                    }
                }
            }
            if (documents.isNotEmpty()) {
                item { Text("کتابخانه فنی", color = Gold, fontWeight = FontWeight.Bold) }
                items(documents, key = { "search-doc-${it.id}" }) { d ->
                    Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
                        Column(Modifier.fillMaxWidth().padding(12.dp)) {
                            Text(d.titleFa, color = Soft, fontWeight = FontWeight.Bold)
                            Text("${d.category} | ${d.brand} ${d.model}", color = Color.LightGray, fontSize = 11.sp)
                        }
                    }
                }
            }
            if (vehicles.isEmpty() && codes.isEmpty() && ecus.isEmpty() && documents.isEmpty()) {
                item { EmptyState("نتیجه‌ای پیدا نشد. عبارت دیگری جستجو کنید.") }
            }
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(14.dp)) {
        Text(message, color = Soft, modifier = Modifier.fillMaxWidth().padding(18.dp), fontSize = 13.sp)
    }
}
