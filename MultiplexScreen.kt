package com.artinkhodro.app

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class MuxLesson(val title: String, val summary: String, val body: String, val tags: String)

private val MUX_LESSONS = listOf(
    MuxLesson("مبانی مالتی‌پلکس و معماری شبکه", "چرا چند یونیت از یک بستر ارتباطی مشترک استفاده می‌کنند؟", "در سیم‌کشی نقطه‌به‌نقطه برای هر فرمان مسیر جداگانه لازم است. در معماری مالتی‌پلکس، یونیت‌ها پیام‌های کدگذاری‌شده را روی یک یا چند شبکه مشترک مبادله می‌کنند. هر یونیت فقط پیام‌های مرتبط با وظیفه خود را پردازش می‌کند. مزیت‌ها شامل کاهش حجم دسته‌سیم، کاهش اتصالات، امکان توسعه قابلیت‌ها و تسهیل عیب‌یابی است.\n\nاجزای معمول: یونیت مرکزی بدنه (BCM/BSI)، یونیت موتور (ECU/PCM)، یونیت ترمز (ABS/ESP)، صفحه کیلومتر، یونیت درها، تهویه، گیربکس (TCU) و درگاه عیب‌یابی. نام و نقش دقیق هر یونیت وابسته به سازنده و پلتفرم است.", "مبانی معماری BSI BCM ECU نود"),
    MuxLesson("CAN؛ ساختار و اصول کار", "شبکه دو سیمه تفاضلی با CAN-H و CAN-L", "در CAN پرسرعت، داده به‌صورت تفاضلی روی دو سیم CAN-H و CAN-L منتقل می‌شود. زوج سیم باید مطابق طراحی خودرو تابیده باقی بماند و انشعاب‌ها/تعمیرات غیراصولی می‌توانند بازتاب و اختلال ایجاد کنند. توپولوژی، نرخ بیت، محل مقاومت‌های انتهایی و تعداد یونیت‌ها را فقط از مستند همان خودرو استخراج کنید.\n\nدر بسیاری از شبکه‌های CAN پرسرعت متداول، دو مقاومت انتهایی ۱۲۰ اهم به‌صورت موازی دیده می‌شوند و اندازه‌گیری بین دو خط در حالت خاموش و پس از خواب شبکه می‌تواند نزدیک ۶۰ اهم باشد؛ این عدد قانون همگانی نیست و برای همه شبکه‌ها/خودروها قابل تعمیم نیست. قبل از اندازه‌گیری، روش سازنده، وضعیت سوئیچ، خواب یونیت‌ها و نقاط مجاز تست را بررسی کنید.", "CAN CAN-H CAN-L مقاومت 60 اهم 120 اهم"),
    MuxLesson("LIN؛ شبکه تک‌سیم", "ارتباط کم‌هزینه برای تجهیزات بدنه", "LIN معمولاً یک خط داده به‌همراه تغذیه و بدنه دارد و به‌صورت فرمان‌دهنده/پاسخ‌دهنده (Master/Slave) کار می‌کند. در کاربردهایی مانند شیشه‌بالابر، آینه، سنسور باران یا تجهیزات صندلی ممکن است استفاده شود؛ کاربرد دقیق به خودرو بستگی دارد.\n\nبرای عیب‌یابی، تغذیه و بدنه یونیت اصلی، سلامت خط LIN، اتصال کوتاه به مثبت/بدنه، پیوستگی سیم و شکل موج را طبق راهنمای سازنده بررسی کنید. ولتاژ ثابت به‌تنهایی اثبات سلامت ارتباط نیست؛ اسیلوسکوپ یا اسکنر سازگار برای مشاهده فعالیت خط مناسب‌تر است.", "LIN master slave تک سیم"),
    MuxLesson("شناسایی نسل‌ها و تفاوت سیستم‌ها", "MAX، ECO-MUX، SMS و معماری‌های اختصاصی", "در مستندات آموزشی ایران‌خودرو، خانواده‌هایی مانند MAX، ECO-MUX و SMS برای برخی محصولات سمند، سورن، رانا، ۲۰۶ و دنا معرفی شده‌اند. در نمونه‌های مستندشده، نام یونیت‌ها و نقش آن‌ها بین نسل‌ها تغییر می‌کند؛ برای مثال CCN/FN در برخی معماری‌ها و BCM/FCM در برخی دیگر دیده می‌شود.\n\nاین نام‌ها و ترکیب نودها را به همه سال‌ها و تیپ‌ها تعمیم ندهید. شناسایی قطعی با VIN، سال تولید، تیپ، شماره فنی یونیت، برچسب سخت‌افزار/نرم‌افزار و منوی شناسایی دستگاه دیاگ انجام شود. جابه‌جایی یونیت یا دانلود نرم‌افزار بدون تطبیق سخت‌افزار و پیکربندی می‌تواند باعث عدم استارت، اختلال تجهیزات یا ناسازگاری امنیتی شود.", "MAX ECO-MUX SMS سمند سورن دنا رانا 206 CCN BCM FCM"),
    MuxLesson("روش عیب‌یابی مرحله‌ای شبکه", "از شکایت مشتری تا تأیید تعمیر", "۱) شکایت را دقیق ثبت و شرایط وقوع را بازتولید کنید. ۲) ولتاژ باتری، شارژ، فیوزها، تغذیه و بدنه یونیت‌ها را بررسی کنید. ۳) با دیاگ تمام یونیت‌ها را اسکن و خطاهای ارتباطی، وضعیت آنلاین/آفلاین و Freeze Frame را ثبت کنید. ۴) نقشه توپولوژی و راهنمای همان تیپ را بررسی کنید. ۵) شبکه را از نظر اتصال کوتاه بین خطوط، اتصال به مثبت/بدنه، قطعی، خوردگی، نفوذ آب و آسیب دسته‌سیم آزمایش کنید. ۶) در صورت مجاز بودن، مقاومت و شکل موج را در نقاط مشخص‌شده بررسی کنید. ۷) پس از تعمیر، خطاها را پاک، اسکن کامل را تکرار و تمام عملکردهای مرتبط را آزمایش کنید.\n\nاز تعویض آزمایشی یونیت، قطع و وصل مکرر باتری، اتصال دادن پایه‌ها یا تزریق ولتاژ به خطوط داده خودداری کنید.", "عیب یابی خطای ارتباطی U-code اسکن دیاگ تغذیه بدنه"),
    MuxLesson("تست الکتریکی و تحلیل نتایج", "مولتی‌متر، اسیلوسکوپ و دستگاه عیب‌یاب", "مولتی‌متر برای بررسی تغذیه، افت ولتاژ، اتصال بدنه و مقاومت در شرایط تعریف‌شده مناسب است. اسیلوسکوپ برای مشاهده شکل موج، نویز، قطع‌شدن متناوب و تقارن سیگنال CAN/LIN کاربرد دارد. اسکنر برای شناسایی یونیت‌ها، خواندن خطاهای U، پارامترها و وضعیت ارتباط استفاده می‌شود.\n\nاندازه‌گیری اهم فقط روی مدار بی‌برق و مطابق دستورالعمل سازنده انجام شود؛ مقاومت‌سنجی روی شبکه فعال می‌تواند به دستگاه یا یونیت آسیب بزند. برای CAN، از اتصال کوتاه کردن CAN-H و CAN-L یا استفاده از لامپ تست رشته‌ای روی خطوط داده جداً خودداری کنید.", "مولتی متر اسیلوسکوپ اهم تست موج"),
    MuxLesson("تعمیر دسته‌سیم و اتصالات شبکه", "حفظ امپدانس و کیفیت زوج تابیده", "محل آسیب را با روش عیب‌یابی تأیید کنید؛ سپس از سیم با سطح مقطع و مشخصات مناسب، اتصال آب‌بندی‌شده و روش تعمیر مورد تأیید سازنده استفاده کنید. زوج CAN باید با گام تابیدگی نزدیک به نمونه اصلی حفظ شود؛ طول بازشدگی زوج و انشعاب‌ها را حداقل نگه دارید. از پیچاندن ساده سیم، چسب برق به‌عنوان تعمیر دائمی، لحیم‌کاری حجیم و ایجاد انشعاب بلند خودداری کنید.\n\nدر برخی دستورالعمل‌های تعمیراتی رنو برای زوج سیم مالتی‌پلکس، استفاده از روکش‌های لحیم‌دار و کیت تعمیر دسته‌سیم مشخص‌شده توصیه شده است؛ روش تعمیر باید از راهنمای همان خودرو پیروی کند.", "تعمیر سیم زوج تابیده کانکتور آب خوردگی"),
    MuxLesson("پیکربندی و دانلود یونیت‌ها", "شناسه سخت‌افزار، نرم‌افزار و پیکربندی", "پیش از برنامه‌ریزی، شارژر تثبیت‌کننده ولتاژ مناسب وصل و سلامت باتری تأیید شود. شماره سخت‌افزار، نسخه نرم‌افزار، کد پیکربندی، نوع موتور/گیربکس و مشخصات خودرو را ثبت کنید. فقط از ابزار و فایل رسمی/مجاز سازگار استفاده کنید و دستورالعمل پشتیبان‌گیری و بازیابی را رعایت کنید.\n\nدر برخی سامانه‌ها انتخاب نادرست نوع ECU یا حالت ارتباطی می‌تواند باعث نمایش اطلاعات اشتباه یا اختلال شبکه شود. دانلود یا تغییر کیلومتر، ایموبیلایزر و داده‌های امنیتی فقط در چارچوب مجاز و با احراز مالکیت انجام شود.", "دانلود پروگرام کالیبراسیون پیکربندی ECU"),
    MuxLesson("نمونه پرونده: سمند، سورن، رانا و ۲۰۶", "نمونه‌های مستندشده، نه پین‌آوت عمومی", "منابع آموزشی کارگیگ برای بعضی نسل‌های MAX، ECO-MUX و SMS، فهرست‌هایی از نودها و نقش‌های CCN، ICN، FN، DDN و PDN و در برخی نسخه‌ها DCN، BCM، FCM و MFD ارائه می‌کنند. برای نمونه، CCN در یک خانواده زیر فرمان و FN در محفظه موتور قرار گرفته‌اند؛ اما این جانمایی را نباید به تمام مدل‌ها تعمیم داد. این فهرست صرفاً نمونه‌ای از معماری‌های مستندشده است. محل نصب، تعداد سوکت، شماره پایه، رنگ سیم و عملکرد هر پایه بین سال، تیپ و سازنده دسته‌سیم تغییر می‌کند.\n\nدر این نسخه عمداً هیچ پین‌آوت، شماره پایه یا نقشه سیم‌کشی عمومی برای این خودروها درج نشده است؛ اطلاعات پایه‌ای فقط پس از تطبیق سند رسمی همان مدل/سال/ECU قابل افزودن خواهد بود.", "سمند سورن رانا پژو 206 نودها"),
    MuxLesson("شبکه در خودروهای چینی و جدیدتر", "CAN چندگانه، Gateway و ارتباطات ترکیبی", "در خودروهای جدید ممکن است چند شبکه CAN با نرخ‌ها و اولویت‌های متفاوت، Gateway مرکزی، LIN و شبکه‌های اختصاصی وجود داشته باشد. برخی پلتفرم‌ها برای سامانه‌های کمک‌راننده، سرگرمی و قوای محرکه شبکه‌های جداگانه دارند. نام‌گذاری یونیت‌ها و پایه‌های درگاه دیاگ به سازنده و سال وابسته است.\n\nبرای خودروهای چینی، از اطلاعات عمومی خودروهای دیگر برای تعیین پایه، مقاومت یا نرخ بیت استفاده نکنید. شناسایی شبکه، ترمینیشن، Gateway و ترتیب بیدارشدن یونیت‌ها باید با راهنمای تعمیراتی همان VIN و ابزار سازگار انجام شود.", "خودرو چینی Gateway CAN FD شبکه"),
    MuxLesson("CAN FD، Gateway و شبکه‌های چندگانه", "شناخت شبکه‌های مدرن و جداسازی دامنه‌های ارتباطی", "در معماری‌های جدید، Gateway پیام‌ها را میان چند دامنه شبکه با نرخ داده یا سطح دسترسی متفاوت عبور می‌دهد. CAN FD امکان payload بزرگ‌تر و فاز داده سریع‌تر را نسبت به CAN کلاسیک فراهم می‌کند؛ اما سازگاری فرستنده، گیرنده، ترنسیور و ابزار عیب‌یابی باید بررسی شود. وجود CAN FD را از روی ظاهر سوکت یا رنگ سیم نمی‌توان تشخیص داد.\n\nدر عیب‌یابی، ابتدا از اسکنر سازگار فهرست یونیت‌ها و شبکه‌های در دسترس را استخراج کنید. اگر چند یونیت یک دامنه هم‌زمان از دسترس خارج‌اند، تغذیه Gateway، وضعیت خواب/بیداری، فیوزها و ارتباط بین دامنه‌ها را بررسی کنید. قطع یک شبکه می‌تواند خطاهای ثانویه در یونیت‌های دیگر ایجاد کند؛ قبل از تعویض قطعه، خطاهای اولیه و ترتیب وقوع آن‌ها را ثبت کنید.", "CAN FD Gateway چند شبکه نرخ داده"),
    MuxLesson("تحلیل خطاهای ارتباطی U-code", "تفکیک خطای شبکه از خرابی خود یونیت", "کدهای خانواده U به اختلال ارتباطی یا داده شبکه اشاره می‌کنند؛ اما به‌تنهایی ثابت نمی‌کنند که یونیت ثبت‌کننده معیوب است. ابتدا مشخص کنید کدام یونیت کد را ثبت کرده، کدام یونیت از دسترس خارج است و خطا در چه شرایطی رخ داده است. ولتاژ پایین باتری، قطع تغذیه، بدنه ضعیف، سوکت سولفاته، Gateway و اختلال شبکه می‌توانند چندین کد U ایجاد کنند.\n\nگزارش کامل همه یونیت‌ها و زمان ثبت خطا را ذخیره کنید. کدها را پیش از ثبت Freeze Frame و شرایط وقوع پاک نکنید. پس از رفع علت اصلی، اسکن مجدد انجام دهید و ببینید کدام خطا بازمی‌گردد. معنی دقیق هر کد و روش آزمون باید از راهنمای همان سازنده و همان ECU استخراج شود.", "U-code خطای ارتباطی شبکه Gateway"),
    MuxLesson("ایمنی تعمیرکار", "حفاظت از یونیت‌ها و سامانه‌های ایمنی", "پیش از کار، دستورالعمل قطع باتری و زمان انتظار سازنده را رعایت کنید. در محدوده ایربگ، پیش‌کشنده کمربند، خودروهای هیبریدی و برقی، بدون آموزش و تجهیزات تخصصی اقدام نکنید. به مدارهای نارنجی ولتاژبالا دست نزنید. از ESD برای کار روی برد و یونیت استفاده کنید و از اتصال کوتاه تصادفی پایه‌های سوکت جلوگیری کنید.\n\nپس از هر تعمیر شبکه، چراغ‌های هشدار، عملکرد ABS/ESP، ایربگ، فرمان برقی، موتور، گیربکس و تجهیزات بدنه مرتبط را طبق روش سازنده کنترل کنید.", "ایمنی SRS ایربگ هیبریدی ولتاژ بالا ESD")
)

@Composable
fun MultiplexScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf<String?>(null) }
    val diagnosticSteps = listOf(
        "ثبت دقیق شکایت و شرایط وقوع عیب",
        "بررسی باتری، شارژ، فیوزها و تغذیه/بدنه یونیت‌ها",
        "اسکن همه یونیت‌ها و ذخیره گزارش خطاها و شرایط وقوع",
        "تطبیق VIN، سال، تیپ، ECU و توپولوژی مستند خودرو",
        "بررسی سوکت‌ها، خوردگی، نفوذ آب و آسیب دسته‌سیم",
        "آزمون مقاومت فقط در مدار بی‌برق و طبق دستورالعمل سازنده",
        "بررسی شکل موج با ابزار و نقاط تست مجاز",
        "تأیید تعمیر، پاک‌کردن خطاها و اسکن نهایی همه یونیت‌ها"
    )
    val completedSteps = remember { mutableStateListOf<String>() }
    val lessons = remember(query) {
        if (query.isBlank()) MUX_LESSONS else MUX_LESSONS.filter {
            (it.title + " " + it.summary + " " + it.body + " " + it.tags).contains(query.trim(), ignoreCase = true)
        }
    }
    Column(modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Text("آموزش تخصصی مالتی‌پلکس", color = Color(0xFFD6AD55), fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("CAN | LIN | نودها | عیب‌یابی | تعمیر شبکه", color = Color(0xFFE8E5DE), fontSize = 12.sp)
        OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(), singleLine = true,
            label = { Text("جست‌وجوی موضوع، یونیت یا کلمه فنی") }, leadingIcon = { Icon(Icons.Default.Search, null) })
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF332A19)), shape = RoundedCornerShape(12.dp)) {
            Text("هشدار فنی: مقادیر مقاومت، پایه‌ها، رنگ سیم و پیکربندی در همه خودروها یکسان نیست. پیش از تست عملی، مستند همان VIN/سال/تیپ/ECU را ملاک قرار دهید.", modifier = Modifier.padding(12.dp), color = Color(0xFFFFDFA0), fontSize = 12.sp)
        }
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF202B25)), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("چک‌لیست تعاملی عیب‌یابی", color = Color(0xFFBFE8C8), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("${completedSteps.size}/${diagnosticSteps.size}", color = Color.White, fontSize = 12.sp)
                    TextButton(onClick = { completedSteps.clear() }) { Text("پاک‌سازی") }
                }
                LinearProgressIndicator(progress = { completedSteps.size.toFloat() / diagnosticSteps.size }, modifier = Modifier.fillMaxWidth(), color = Color(0xFF8BC99A))
                diagnosticSteps.forEach { step ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = completedSteps.contains(step), onCheckedChange = { checked ->
                            if (checked) completedSteps.add(step) else completedSteps.remove(step)
                        })
                        Text(step, color = Color(0xFFE8E5DE), fontSize = 11.sp)
                    }
                }
                Text("این چک‌لیست راهنمای عمومی است؛ ترتیب و روش آزمون نهایی باید با دستورالعمل سازنده خودرو تطبیق داده شود.", color = Color(0xFFFFDFA0), fontSize = 10.sp)
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(lessons, key = { it.title }) { lesson ->
                val isOpen = expanded == lesson.title
                Card(onClick = { expanded = if (isOpen) null else lesson.title }, colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1E23))) {
                    Column(Modifier.fillMaxWidth().padding(13.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text(lesson.title, color = Color(0xFFD6AD55), fontWeight = FontWeight.Bold)
                        Text(lesson.summary, color = Color(0xFFE8E5DE), fontSize = 12.sp)
                        if (isOpen) {
                            Divider(color = Color.DarkGray)
                            Text(lesson.body, color = Color(0xFFE8E5DE), fontSize = 12.sp, lineHeight = 20.sp)
                        }
                        Text(if (isOpen) "بستن ▲" else "مطالعه درس ▼", color = Color(0xFFD6AD55), fontSize = 11.sp)
                    }
                }
            }
            item {
                Text("منابع مطالعه و تطبیق", color = Color(0xFFD6AD55), fontWeight = FontWeight.Bold)
                val sources = listOf(
                    "جزوه آموزشی سیستم مالتی‌پلکس کارگیگ" to "https://cargeek.live/docs/%D9%85%D8%A7%D9%84%D8%AA%DB%8C%20%D8%A8%D9%84%DA%A9%D8%B3_0REj34w.pdf",
                    "پیکره‌بندی سیستم‌های MAX و ECO-MUX و SMS" to "https://cargeek.live/docs/%D9%BE%DB%8C%DA%A9%D8%B1%D9%87%20%D8%A8%D9%86%D8%AF%DB%8C%20%D9%85%D8%A7%D9%84%D8%AA%DB%8C%20%D9%BE%D9%84%D9%83%D8%B3%D9%87%D8%A7%20%201_mo0KRWb.pdf",
                    "راهنمای مالتی‌پلکس SMS سمند و سورن" to "https://cargeek.live/d/8SyenYTV",
                    "راهنمای ECO-MUX سمند و سورن" to "https://cargeek.live/d/BMtaoZ2i",
                    "راهنمای مالتی‌پلکس SMS دنا" to "https://cargeek.live/d/RlPkDOcq",
                    "راهنمای عیب‌یابی و پیکربندی SMS دنا و سمند" to "https://cargeek.live/d/pSW5HXoU",
                    "راهنمای برق و سیستم‌های الکتریکی تیگو ۵" to "https://cargeek.live/docs/Chery_tiggo5_electronics_ServiceManual.pdf",
                    "راهنمای مدارهای الکتریکی پژو ۲۰۶ فاز ۱ / ECO-MUX" to "https://cargeek.live/d/sT5KhPNr",
                    "فهرست مستندات مالتی‌پلکس کارگیگ" to "https://cargeek.live/t/%D9%85%D8%A7%D9%84%D8%AA%DB%8C%20%D9%BE%D9%84%DA%A9%D8%B3"
                )
                sources.forEach { (title, url) ->
                    TextButton(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }) {
                        Text(title, color = Color(0xFFE8E5DE), modifier = Modifier.weight(1f))
                        Icon(Icons.Default.OpenInNew, null, tint = Color(0xFFD6AD55))
                    }
                }
                Text("محتوای آموزشی این بخش به‌صورت خلاصه و بازنویسی‌شده تدوین شده است؛ فایل‌های کامل اشخاص ثالث در APK بازنشر نشده‌اند.", color = Color.LightGray, fontSize = 10.sp)
            }
        }
    }
}
