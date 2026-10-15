package com.example.aplicacion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@Composable
fun ScreenProducts(navController: NavHostController, servicio: ProductApiService) {
    val listaProductos = remember { mutableStateListOf<ProductModel>() }
    var cargando by remember { mutableStateOf(true) }

    // Equivalente al LaunchedEffect del laboratorio
    LaunchedEffect(Unit) {
        try {
            val respuesta = servicio.getProducts()
            listaProductos.clear()
            listaProductos.addAll(respuesta.products)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cargando = false
        }
    }

    if (cargando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // Implementación visualmente llamativa usando Cards (Tarjetas) y Coil (Imágenes)
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(listaProductos) { producto ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            navController.navigate("productDetail/${producto.id}") 
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = producto.thumbnail,
                            contentDescription = producto.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = producto.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                maxLines = 1
                            )
                            Text(
                                text = producto.category.uppercase(),
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$${producto.price}",
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenProductDetail(navController: NavHostController, servicio: ProductApiService, id: Int) {
    var producto by remember { mutableStateOf<ProductModel?>(null) }

    LaunchedEffect(Unit) {
        try {
            producto = servicio.getProductById(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Pantalla de detalles también con un diseño bonito
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (producto != null) {
                AsyncImage(
                    model = producto!!.thumbnail,
                    contentDescription = producto!!.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = producto!!.title, fontWeight = FontWeight.Bold, fontSize = 26.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$${producto!!.price}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = producto!!.description,
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                CircularProgressIndicator(modifier = Modifier.padding(top = 100.dp))
            }
        }
    }
}
