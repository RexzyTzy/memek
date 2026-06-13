package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch
import java.net.URLEncoder

// Screen configuration definitions
enum class ActiveScreen(val title: String) {
    HOME("Home & Info"),
    LIST_HOSTING("List Hosting"),
    JASA_DEVELOPER("Jasa Developer"),
    PAYMENT("Metode Payment"),
    CREDITS("Credits & Team")
}

// Data models for the products
data class HostingProduct(
    val title: String,
    val subtitle: String,
    val slots: List<HostingSlot>
)

data class HostingSlot(
    val slotText: String,
    val price: String
)

data class DevService(
    val name: String,
    val price: String,
    val isAvailable: Boolean = true
)

data class DevCategory(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val services: List<DevService>
)

data class UserFeedback(
    val name: String,
    val rating: Int,
    val comment: String,
    val date: String = "Baru Saja"
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(ActiveScreen.HOME) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Host order details
    var selectedProductTitle by remember { mutableStateOf("") }
    var selectedSlotInfo by remember { mutableStateOf("") }
    var selectedPriceInfo by remember { mutableStateOf("") }
    var showPurchaseDialog by remember { mutableStateOf(false) }

    // Service order details
    var selectedServiceTitle by remember { mutableStateOf("") }
    var selectedServicePrice by remember { mutableStateOf("") }
    var showDevPurchaseDialog by remember { mutableStateOf(false) }

    // Setup predefined comments & ratings feed for interactive feel in Home Tab
    val reviews = remember {
        mutableStateListOf(
            UserFeedback("Rian Samp", 5, "Server Ryzen 9 ngebut abis! No lag buat 100 player online sekaligus! Recommended seller ⚡"),
            UserFeedback("Noah_Gamer", 4, "Penanganan cepat, panel pterodactyl gampang digunain dan setup cuman makan waktu 5 menit."),
            UserFeedback("DimasGta", 5, "Jasa build bot whatsapp-nya ngebantu banget buat control server rcon dari jarak jauh. Mantap Rex!"),
            UserFeedback("Fajar_Maulana", 5, "Anti DDoS-nya beneran aman dari hantaman bocil ddosser. Sukses terus Rex Hosting!")
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(310.dp)
            ) {
                // Branded Sidebar Header with lightblue theme gradients
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF0284C7), Color(0xFF38BDF8))
                            )
                        )
                        .padding(vertical = 32.dp, horizontal = 24.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "Rex Logo",
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "REX HOSTING",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Performa Premium • Harga Hemat",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sidebar items
                ActiveScreen.values().forEach { screen ->
                    val isSelected = currentScreen == screen
                    val icon = when (screen) {
                        ActiveScreen.HOME -> Icons.Default.Home
                        ActiveScreen.LIST_HOSTING -> Icons.Default.Cloud
                        ActiveScreen.JASA_DEVELOPER -> Icons.Default.Code
                        ActiveScreen.PAYMENT -> Icons.Default.Payments
                        ActiveScreen.CREDITS -> Icons.Default.Group
                    }

                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = screen.title,
                                tint = if (isSelected) Color(0xFF0284C7) else Color(0xFF64748B)
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 15.sp,
                                color = if (isSelected) Color(0xFF0284C7) else Color(0xFF334155)
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            currentScreen = screen
                            coroutineScope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color(0xFFF0F9FF),
                            unselectedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .height(52.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                
                // Footer brand info
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column {
                        Text(
                            text = "Aplikasi v1.0 • Client Mode",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "Powered by Rex Hosting Official",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0284C7)
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu Sidebar",
                                tint = Color.White
                            )
                        }
                    },
                    title = {
                        Column {
                            Text(
                                text = "Rex Hosting",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                            Text(
                                text = currentScreen.title,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0284C7)
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        // Quick call admin support action
                        val text = URLEncoder.encode("*HALLO ADMIN REX HOSTING SAYA BUTUH SUPPORT / TANYA SEPUTAR HOSTING*", "UTF-8")
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://api.whatsapp.com/send?phone=6283899782135&text=$text")
                        }
                        context.startActivity(intent)
                    },
                    containerColor = Color(0xFF0284C7),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = "Bantuan Admin WhatsApp",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            containerColor = Color(0xFFF0F9FF)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    ActiveScreen.HOME -> HomeScreen(reviews)
                    ActiveScreen.LIST_HOSTING -> HostingListScreen { prodTitle, slotText, price ->
                        selectedProductTitle = prodTitle
                        selectedSlotInfo = slotText
                        selectedPriceInfo = price
                        showPurchaseDialog = true
                    }
                    ActiveScreen.JASA_DEVELOPER -> JasaDeveloperScreen { serviceTitle, price ->
                        selectedServiceTitle = serviceTitle
                        selectedServicePrice = price
                        showDevPurchaseDialog = true
                    }
                    ActiveScreen.PAYMENT -> PaymentScreen()
                    ActiveScreen.CREDITS -> CreditsScreen()
                }

                // Buy Hosting Dialog Popup
                if (showPurchaseDialog) {
                    BuyHostingDialog(
                        onDismiss = { showPurchaseDialog = false },
                        productTitle = selectedProductTitle,
                        slotInfo = selectedSlotInfo,
                        priceInfo = selectedPriceInfo
                    )
                }

                // Buy Dev Services Dialog Popup
                if (showDevPurchaseDialog) {
                    BuyDeveloperDialog(
                        onDismiss = { showDevPurchaseDialog = false },
                        serviceTitle = selectedServiceTitle,
                        priceInfo = selectedServicePrice
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 1. HOME SCREEN
// -------------------------------------------------------------
@Composable
fun HomeScreen(reviews: MutableList<UserFeedback>) {
    var reviewerName by remember { mutableStateOf("") }
    var reviewerStars by remember { mutableIntStateOf(5) }
    var reviewerText by remember { mutableStateOf("") }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF0F172A), Color(0xFF0284C7))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "⚡ REX HOSTING STORE",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Temukan Server & Solusi SA-MP Terbaik Di Sini!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Kami menyediakan hosting server multiplayer berspesifikasi super handal untuk menunjang kebutuhan komunitas GTA San Andreas Anda agar online terus 24 jam penuh tanpa henti.",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Penjelasan & Fungsi Section
        item {
            Column {
                Text(
                    text = "Tentang Rex Hosting",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "Cloud",
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Penjelasan Layanan",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Rex Hosting adalah marketplace hosting server SA-MP premium terbesar yang menawarkan hardware canggih seperti AMD Ryzen 9 dan AMD Epyc Milan untuk menjamin stabilitas permainan tanpa jitter ataupun rollback data.",
                            fontSize = 13.sp,
                            color = Color(0xFF475569),
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Computer,
                                contentDescription = "Computer",
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Fungsi Aplikasi",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sebagai platform katalog satu pintu (one-stop marketplace) bagi pengelola server GTA SAKP: memudahkan penyewaan slot server hosting, perekrutan jasa developer skrip handal, penyediaan bot manajemen whatsapp/discord, serta rincian pembayaran instan terverifikasi.",
                            fontSize = 13.sp,
                            color = Color(0xFF475569),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // Stats Rating summary
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text(
                            text = "4.9",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0284C7)
                        )
                        Row {
                            repeat(5) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Star",
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Text(
                            text = "Total 148 Ulasan",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    
                    Spacer(
                        modifier = Modifier
                            .width(1.dp)
                            .height(60.dp)
                            .background(Color(0xFFE2E8F0))
                    )

                    Column(
                        modifier = Modifier
                            .weight(1.5f)
                            .padding(start = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "⚡ Jaminan Kepuasan 100%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "✓ Pengerjaan Cepat & Handal",
                            fontSize = 12.sp,
                            color = Color(0xFF475569)
                        )
                        Text(
                            text = "✓ Support Teknis Tanggap 24/7",
                            fontSize = 12.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }
            }
        }

        // Add Customer review interactively
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tulis Ulasan Anda",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = reviewerName,
                        onValueChange = { reviewerName = it },
                        label = { Text("Nama Pengguna") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0284C7),
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(text = "Rating Anda :  ", fontSize = 13.sp, color = Color(0xFF475569))
                        repeat(5) { index ->
                            val starIndex = index + 1
                            Icon(
                                imageVector = if (starIndex <= reviewerStars) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Bintang",
                                tint = if (starIndex <= reviewerStars) Color(0xFFF59E0B) else Color(0xFF94A3B8),
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { reviewerStars = starIndex }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = reviewerText,
                        onValueChange = { reviewerText = it },
                        label = { Text("Isi Ulasan / Pengalaman") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0284C7),
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (reviewerName.trim().isEmpty() || reviewerText.trim().isEmpty()) {
                                Toast.makeText(context, "Lengkapi nama dan ulasan Anda!", Toast.LENGTH_SHORT).show()
                            } else {
                                reviews.add(0, UserFeedback(reviewerName, reviewerStars, reviewerText))
                                reviewerName = ""
                                reviewerText = ""
                                reviewerStars = 5
                                Toast.makeText(context, "Terima kasih atas ulasan ulasan positif Anda! 😍", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Kirim", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Kirim Ulasan")
                    }
                }
            }
        }

        // Live Reviews Feed
        item {
            Text(
                text = "Ulasan Pengguna (${reviews.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(reviews) { feed ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0F2FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User",
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = feed.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF0F172A)
                            )
                        }
                        
                        Text(
                            text = feed.date,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(modifier = Modifier.padding(bottom = 6.dp)) {
                        repeat(feed.rating) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Star",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    
                    Text(
                        text = feed.comment,
                        fontSize = 12.sp,
                        color = Color(0xFF334155),
                        lineHeight = 16.sp
                    )
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// -------------------------------------------------------------
// 2. LIST HOSTING SCREEN
// -------------------------------------------------------------
@Composable
fun HostingListScreen(onBuyClicked: (productName: String, slotText: String, priceText: String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    
    val products = listOf(
        HostingProduct(
            title = "AMD RYZEN 9 7950X (SA-MP)",
            subtitle = "Performa Tinggi • Cocok Server Besar",
            slots = listOf(
                HostingSlot("30 SLOT PLAYER", "30K"),
                HostingSlot("50 SLOT PLAYER", "45K"),
                HostingSlot("80 SLOT PLAYER", "60K"),
                HostingSlot("100 SLOT PLAYER", "75K")
            )
        ),
        HostingProduct(
            title = "AMD EPYC MILAN 7763 (SA-MP)",
            subtitle = "Harga Hemat • Stabil Komunitas",
            slots = listOf(
                HostingSlot("30 SLOT PLAYER", "20K"),
                HostingSlot("50 SLOT PLAYER", "30K"),
                HostingSlot("80 SLOT PLAYER", "40K"),
                HostingSlot("100 SLOT PLAYER", "55K")
            )
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Features highlight Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "⚡ FITUR HOSTING REX",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0284C7)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val features = listOf(
                        "Server Online 24/7",
                        "Anti DDoS Protection (Cloudflare + Tencent)",
                        "NVMe SSD Storage",
                        "Panel Pterodactyl",
                        "Full FTP Access",
                        "Setup Cepat",
                        "Support 24 Jam"
                    )

                    features.forEach { feature ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Check",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = feature,
                                fontSize = 13.sp,
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Search Input Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari server hosting...", color = Color(0xFF94A3B8)) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF64748B)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF0284C7),
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Hostings lists filtered
        val filteredProducts = products.filter { product ->
            product.title.contains(searchQuery, ignoreCase = true) ||
            product.subtitle.contains(searchQuery, ignoreCase = true)
        }

        if (filteredProducts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Hasil pencarian tidak ditemukan.", color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                }
            }
        }

        items(filteredProducts) { itemHardware ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = itemHardware.title,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = itemHardware.subtitle,
                                fontSize = 12.sp,
                                color = Color(0xFF0284C7),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE0F2FE), CircleShape)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "Cloud Node",
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .height(1.dp)
                            .background(Color(0xFFF1F5F9))
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        itemHardware.slots.forEach { option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                    .padding(vertical = 8.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = option.slotText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "Harga : Rp. ${option.price}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                }
                                
                                Button(
                                    onClick = { onBuyClicked(itemHardware.title, option.slotText, option.price) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text(text = "Beli Sekarang", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Keunggulan details card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "☄ KEUNGGULAN REX HOSTING",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFF59E0B)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val advantages = listOf(
                        "Uptime 99.9%",
                        "Network 10Gbps",
                        "Anti DDoS Layer 3 / 4 / 7",
                        "Server Stabil No Lag"
                    )

                    advantages.forEach { advant ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = advant,
                                fontSize = 13.sp,
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// -------------------------------------------------------------
// 3. JASA DEVELOPER SCREEN
// -------------------------------------------------------------
@Composable
fun JasaDeveloperScreen(onOrderClicked: (serviceName: String, priceText: String) -> Unit) {
    var queryDev by remember { mutableStateOf("") }

    val categories = listOf(
        DevCategory(
            title = "JASA DEVELOPER SERVERS SAMP",
            icon = Icons.Default.Cloud,
            services = listOf(
                DevService("Jasa developer server inferno basic", "30K/bulan"),
                DevService("Jasa developer server inferno modern", "50K/bulan"),
                DevService("Jasa developer server lrp modern", "75K/bulan"),
                DevService("Jasa developer server lrp old school", "85K/bulan"),
                DevService("Jasa developer server open mp all type", "135K/bulan")
            )
        ),
        DevCategory(
            title = "JASA DEVELOPER WEB & APP",
            icon = Icons.Default.Code,
            services = listOf(
                DevService("Jasa build website static", "60K"),
                DevService("Jasa build app native", "90K"),
                DevService("Jasa build website full stack", "255K"),
                DevService("Jasa build app big project", "150K")
            )
        ),
        DevCategory(
            title = "JASA BOT & AI SYSTEMS",
            icon = Icons.Default.Computer,
            services = listOf(
                DevService("Jasa create bot whatsapp", "80K"),
                DevService("Jasa create bot discord", "110K"),
                DevService("Jasa create website dan application ai dengan api key dari kita", "340K"),
                DevService("Jasa create website dan application ai dengan api key dari kamu", "230K")
            )
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Explanatory note
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFF0F9FF), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Assignment, contentDescription = "Note", tint = Color(0xFF0284C7))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🛠️ Jasa Developer Handal",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Nikmati pengerjaan script, bot custom, website responsif, dan app fullstack yang diselesaikan secara professional oleh pakarnya.",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Search Bar for Dev Services
        item {
            OutlinedTextField(
                value = queryDev,
                onValueChange = { queryDev = it },
                placeholder = { Text("Cari jasa dev...", color = Color(0xFF94A3B8)) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF64748B)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF0284C7),
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        categories.forEach { category ->
            // Filter services inside category based on query
            val matchingServices = category.services.filter {
                it.name.contains(queryDev, ignoreCase = true)
            }

            if (matchingServices.isNotEmpty()) {
                item {
                    Text(
                        text = category.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                items(matchingServices) { service ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.8f)) {
                                Text(
                                    text = service.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF334155),
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFECFDF5), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Aktif",
                                            color = Color(0xFF10B981),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Harga : Rp. ${service.price}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0284C7)
                                    )
                                }
                            }
                            
                            Button(
                                onClick = { onOrderClicked(service.name, service.price) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                            ) {
                                Text(text = "Sewa Jasa", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// -------------------------------------------------------------
// 4. PAYMENT SCREEN
// -------------------------------------------------------------
@Composable
fun PaymentScreen() {
    val context = LocalContext.current
    
    val payments = listOf(
        mapOf("name" to "DANA", "val" to "083866940058", "status" to "ready"),
        mapOf("name" to "GOPAY", "val" to "Unknown", "status" to "not ready"),
        mapOf("name" to "OVO", "val" to "083866940058", "status" to "ready")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF0284C7), Color(0xFF0284C7).copy(alpha = 0.8f))
                    )
                ).padding(24.dp)) {
                    Text(
                        text = "💳 GERBANG PEMBAYARAN REX",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Metode Pembayaran Resmi",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Gunakan nomor di bawah untuk melakukan checkout pesanan. Diwajibkan mengirim bukti transfer yang sah ke customer support setelah transaksi berhasil.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        items(payments) { pay ->
            val isReady = pay["status"] == "ready"
            
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = pay["name"] ?: "",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        
                        // Status Badge Green/White for Ready, Red/White for Not Ready
                        val badgeBg = if (isReady) Color(0xFF22C55E) else Color(0xFFEF4444)
                        val badgeTxt = Color.White

                        Box(
                            modifier = Modifier
                                .background(badgeBg, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = (pay["status"] ?: "").uppercase(),
                                color = badgeTxt,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Nomor Rekening / E-Wallet",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = pay["val"] ?: "",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155)
                            )
                        }
                        
                        // Copy clipboard action if number is actual value
                        if (isReady && pay["val"] != "Unknown") {
                            IconButton(onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Copied Number", pay["val"])
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "${pay["name"]} nomor berhasil disalin ke clipboard! 📋", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Salin Nomor",
                                    tint = Color(0xFF0284C7)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. CREDITS SCREEN
// -------------------------------------------------------------
@Composable
fun CreditsScreen() {
    val owners = listOf("Rex", "Noah", "Luiz", "Mey")
    val partners = listOf<String>() // Kosong

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Info Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color(0xFFE0F2FE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = "Team",
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Rex Hosting Team",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Pengelola & Rekan Resmi",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        // Owners sections
        item {
            Text(
                text = "LIST OWNER",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(owners) { ownerName ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF0284C7), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ownerName.first().toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = ownerName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Owner / Administrator Utama",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        // Partners section
        item {
            Text(
                text = "LIST PARTNER",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (partners.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "kosong",
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// POPUP DIALOGS
// -------------------------------------------------------------
@Composable
fun BuyHostingDialog(
    onDismiss: () -> Unit,
    productTitle: String,
    slotInfo: String,
    priceInfo: String
) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedPayment by remember { mutableStateOf("DANA") }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Konfirmasi Checkout",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = Color(0xFF64748B))
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Item details info
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0F9FF), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = productTitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7)
                        )
                        Text(
                            text = slotInfo,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Harga : Rp. $priceInfo",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Inputs
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Gmail") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0284C7),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0284C7),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0284C7),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Payment selector (DANA / OVO)
                Text(
                    text = "Pilih Metode Pembayaran :",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedPayment == "DANA",
                            onClick = { selectedPayment = "DANA" },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0284C7))
                        )
                        Text(text = "DANA", fontSize = 13.sp, modifier = Modifier.clickable { selectedPayment = "DANA" })
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedPayment == "OVO",
                            onClick = { selectedPayment = "OVO" },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0284C7))
                        )
                        Text(text = "OVO", fontSize = 13.sp, modifier = Modifier.clickable { selectedPayment = "OVO" })
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val trimmedEmail = email.trim()
                        val trimmedUser = username.trim()
                        val trimmedPass = password.trim()
                        
                        if (trimmedEmail.isEmpty() || trimmedUser.isEmpty() || trimmedPass.isEmpty()) {
                            Toast.makeText(context, "Harap lengkapi semua data formulir silakan!", Toast.LENGTH_SHORT).show()
                        } else if (!trimmedEmail.contains("@")) {
                            Toast.makeText(context, "Masukan alamat gmail yang valid!", Toast.LENGTH_SHORT).show()
                        } else {
                            val hostType = "$productTitle - $slotInfo ($priceInfo)"
                            val textMessage = """
                                *HALLO ADMIN REX HOSTING SAYA INGIN BELI PRODUK KAMU*
                                *BERIKUT SPESIFIK YANG AKU INGINKAN*
                                
                                gmail : $trimmedEmail
                                username : $trimmedUser
                                password : $trimmedPass
                                type hosting : $hostType
                                payment : $selectedPayment
                                
                                *NOTE : TUNGGU ADMIN BALES, DAN DI LARANG KERAS SPAM TERHADAP ADMIN!!*
                            """.trimIndent()
                            
                            try {
                                val encodedText = URLEncoder.encode(textMessage, "UTF-8")
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://api.whatsapp.com/send?phone=6283899782135&text=$encodedText")
                                }
                                context.startActivity(intent)
                                onDismiss()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error mengarahkan ke WhatsApp", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Confirmasi Pembelian")
                }
            }
        }
    }
}

@Composable
fun BuyDeveloperDialog(
    onDismiss: () -> Unit,
    serviceTitle: String,
    priceInfo: String
) {
    var namaPengguna by remember { mutableStateOf("") }
    var detailCustom by remember { mutableStateOf("") }
    var paymentOption by remember { mutableStateOf("DANA") }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Konfirmasi Pemesanan Jasa",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = Color(0xFF64748B))
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "JASA YANG DIPILIH :",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = serviceTitle,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0284C7)
                        )
                        Text(
                            text = "Harga : Rp. $priceInfo",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = namaPengguna,
                    onValueChange = { namaPengguna = it },
                    label = { Text("Nama Pengguna / Akun") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0284C7),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = detailCustom,
                    onValueChange = { detailCustom = it },
                    label = { Text("Catatan Custom (Opsional)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0284C7),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Pilih Rencana Pembayaran :",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = paymentOption == "DANA",
                            onClick = { paymentOption = "DANA" },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0284C7))
                        )
                        Text(text = "DANA", fontSize = 13.sp, modifier = Modifier.clickable { paymentOption = "DANA" })
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = paymentOption == "OVO",
                            onClick = { paymentOption = "OVO" },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0284C7))
                        )
                        Text(text = "OVO", fontSize = 13.sp, modifier = Modifier.clickable { paymentOption = "OVO" })
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val trimmedNama = namaPengguna.trim()
                        if (trimmedNama.isEmpty()) {
                            Toast.makeText(context, "Harap lengkapi nama pemesan!", Toast.LENGTH_SHORT).show()
                        } else {
                            val notes = if (detailCustom.trim().isEmpty()) "-" else detailCustom.trim()
                            val textMessage = """
                                *HALLO ADMIN REX HOSTING SAYA UTUSAN UNTUK PESAN JASA DEVELOPER*
                                *BERIKUT INTEGRASI DETAIL YANG SAYA INGINKAN*
                                
                                gmail : - (jasa dev)
                                username : $trimmedNama
                                password : (custom requirements: $notes)
                                type hosting : Jasa Dev - $serviceTitle ($priceInfo)
                                payment : $paymentOption
                                
                                *NOTE : TUNGGU ADMIN BALES, DAN DI LARANG KERAS SPAM TERHADAP ADMIN!!*
                            """.trimIndent()
                            
                            try {
                                val encodedText = URLEncoder.encode(textMessage, "UTF-8")
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://api.whatsapp.com/send?phone=6283899782135&text=$encodedText")
                                }
                                context.startActivity(intent)
                                onDismiss()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error mengarahkan ke WhatsApp", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Confirmasi Pemesanan")
                }
            }
        }
    }
}
